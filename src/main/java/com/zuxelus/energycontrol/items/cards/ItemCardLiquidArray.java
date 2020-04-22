package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.crossmod.LiquidCardHelper;
import com.zuxelus.energycontrol.utils.CardState;
import com.zuxelus.energycontrol.utils.PanelSetting;
import com.zuxelus.energycontrol.utils.PanelString;
import com.zuxelus.energycontrol.utils.StringUtils;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class ItemCardLiquidArray extends ItemCardBase {
	private static final int STATUS_NOT_FOUND = Integer.MIN_VALUE;
	private static final int STATUS_OUT_OF_RANGE = Integer.MIN_VALUE + 1;	
	
	public ItemCardLiquidArray() {
		super(ItemCardType.CARD_LIQUID_ARRAY, "card_liquid_array");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.card_liquid_array";
	}

	@Override
	public CardState update(World world, ItemCardReader reader, int range, BlockPos pos) {
		int cardCount = reader.getCardCount();
		if (cardCount == 0)
			return CardState.INVALID_CARD;

		double totalAmount = 0.0;

		boolean foundAny = false;
		boolean outOfRange = false;
		int liquidId = 0;
		for (int i = 0; i < cardCount; i++) {
			BlockPos target = getCoordinates(reader, i);
			int dx = target.getX() - pos.getX();
			int dy = target.getY() - pos.getY();
			int dz = target.getZ() - pos.getZ();
			if (Math.abs(dx) <= range && Math.abs(dy) <= range && Math.abs(dz) <= range) {
				IFluidTankProperties storage = LiquidCardHelper.getStorageAt(world, target);
				if (storage != null) {
					FluidStack stack = storage.getContents(); 
					if (stack != null) {
						totalAmount += stack.amount;
						reader.setInt(String.format("_%damount", i),stack.amount);
						String name = I18n.format("msg.ec.None");
						if (stack.amount > 0)
							name = FluidRegistry.getFluidName(stack);
						reader.setString(String.format("_%dname", i), name);
					}
					reader.setInt(String.format("_%dcapacity", i), storage.getCapacity());
					foundAny = true;
				} else
					reader.setInt(String.format("_%damount", i), STATUS_NOT_FOUND);
			} else {
				reader.setInt(String.format("_%damount", i), STATUS_OUT_OF_RANGE);
				outOfRange = true;
			}
		}
		reader.setDouble("energy", totalAmount);
		if (!foundAny) {
			if (outOfRange)
				return CardState.OUT_OF_RANGE;
			return CardState.NO_TARGET;
		}
		return CardState.OK;
	}

	@Override
	protected List<PanelString> getStringData(int displaySettings, ItemCardReader reader, boolean showLabels) {
		List<PanelString> result = new LinkedList<PanelString>();
		double totalAmount = 0;
		double totalCapacity = 0;
		boolean showEach = (displaySettings & 1) > 0;
		boolean showSummary = (displaySettings & 2) > 0;
		boolean showName = (displaySettings & 4) > 0;
		boolean showAmount = true;// (displaySettings & DISPLAY_AMOUNT) > 0;
		boolean showFree = (displaySettings & 8) > 0;
		boolean showCapacity = (displaySettings & 16) > 0;
		boolean showPercentage = (displaySettings & 32) > 0;
		for (int i = 0; i < reader.getInt("cardCount"); i++) {
			int amount = reader.getInt(String.format("_%damount", i));
			int capacity = reader.getInt(String.format("_%dcapacity", i));
			boolean isOutOfRange = amount == STATUS_OUT_OF_RANGE;
			boolean isNotFound = amount == STATUS_NOT_FOUND;
			if (showSummary && !isOutOfRange && !isNotFound) {
				totalAmount += amount;
				totalCapacity += capacity;
			}

			if (showEach) {
				if (isOutOfRange) {
					result.add(new PanelString(StringUtils.getFormattedKey("msg.nc.InfoPanelOutOfRangeN", i + 1)));
				} else if (isNotFound) {
					result.add(new PanelString(StringUtils.getFormattedKey("msg.nc.InfoPanelNotFoundN", i + 1)));
				} else {
					if (showName) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.nc.InfoPanelLiquidNameN", i + 1,
									reader.getString(String.format("_%dname", i)))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", amount, false)));
					}
					if (showAmount) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.nc.InfoPanelLiquidN", i + 1,
									StringUtils.getFormatted("", amount, false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", amount, false)));
					}
					if (showFree) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.nc.InfoPanelLiquidFreeN", i + 1,
									StringUtils.getFormatted("", capacity - amount, false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", capacity - amount, false)));
					}
					if (showCapacity) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.nc.InfoPanelLiquidCapacityN",
									i + 1, StringUtils.getFormatted("", capacity, false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", capacity, false)));
					}
					if (showPercentage) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.nc.InfoPanelLiquidPercentageN",
									i + 1, StringUtils.getFormatted("",
											capacity == 0 ? 100 : (((double) amount / capacity) * 100), false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("",
									capacity == 0 ? 100 : (((double) amount / capacity) * 100), false)));
					}
				}
			}
		}
		if (showSummary) {
			if (showAmount)
				result.add(new PanelString("msg.nc.InfoPanelLiquidAmount", totalAmount, showLabels));
			if (showFree)
				result.add(new PanelString("msg.nc.InfoPanelLiquidFree", totalCapacity - totalAmount, showLabels));
			if (showName)
				result.add(new PanelString("msg.nc.InfoPanelLiquidCapacity", totalCapacity, showLabels));
			if (showPercentage)
				result.add(new PanelString("msg.nc.InfoPanelLiquidPercentage",
						totalCapacity == 0 ? 100 : ((totalAmount / totalCapacity) * 100), showLabels));
		}
		return result;
	}

	@Override
	protected List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(6);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergyCurrent"), 1,damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergyStorage"), 2,damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergyFree"), 4,damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergyPercentage"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergyEach"), 16,damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergyTotal"), 32,damage));
		return result;
	}
}