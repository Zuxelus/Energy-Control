package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.EnergyStorageData;
import com.zuxelus.energycontrol.utils.CardState;
import com.zuxelus.energycontrol.utils.PanelSetting;
import com.zuxelus.energycontrol.utils.PanelString;
import com.zuxelus.energycontrol.utils.StringUtils;

import ic2.api.energy.EnergyNet;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardGeneratorArray extends ItemCardBase {
	private static final int STATUS_NOT_FOUND = Integer.MIN_VALUE;
	private static final int STATUS_OUT_OF_RANGE = Integer.MIN_VALUE + 1;	

	public ItemCardGeneratorArray() {
		super(ItemCardType.CARD_GENERATOR_ARRAY, "card_generator_array");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.card_generator_array";
	}

	@Override
	public CardState update(World world, ItemCardReader reader, int range, BlockPos pos) {
		int cardCount = reader.getCardCount();
		if (cardCount == 0)
			return CardState.INVALID_CARD;

		double totalEnergy = 0.0;
		boolean foundAny = false;
		boolean outOfRange = false;
		for (int i = 0; i < cardCount; i++) {
			BlockPos target = getCoordinates(reader, i);
			int dx = target.getX() - pos.getX();
			int dy = target.getY() - pos.getY();
			int dz = target.getZ() - pos.getZ();
			if (Math.abs(dx) <= range && Math.abs(dy) <= range && Math.abs(dz) <= range) {
				TileEntity entity = world.getTileEntity(target);
				NBTTagCompound tag = CrossModLoader.crossIc2.getGeneratorData(entity);
				if (tag != null) {
						reader.setDouble(String.format("_%dstorage", i), tag.getDouble("storage"));
						reader.setDouble(String.format("_%dmaxStorage", i), tag.getDouble("maxStorage"));
						reader.setDouble(String.format("_%dproduction", i), tag.getDouble("production"));
					foundAny = true;
				} else
					reader.setInt(String.format("_%denergy", i), STATUS_NOT_FOUND);
			} else {
				reader.setInt(String.format("_%denergy", i), STATUS_OUT_OF_RANGE);
				outOfRange = true;
			}
		}
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
		PanelString line;
		double totalEnergy = 0;
		double totalStorage = 0;
		double totalOutput = 0;
		boolean showSummary = (displaySettings & 16) > 0;
		for (int i = 0; i < reader.getCardCount(); i++) {
			double energy = reader.getDouble(String.format("_%dstorage", i));
			int storage = reader.getInt(String.format("_%dmaxStorage", i));
			int output = reader.getInt(String.format("_%dproduction", i));
			boolean isOutOfRange = energy == STATUS_OUT_OF_RANGE;
			boolean isNotFound = energy == STATUS_NOT_FOUND;
			if (showSummary && !isOutOfRange && !isNotFound) {
				totalEnergy += energy;
				totalStorage += storage;
				totalOutput += output;
			}
		
			if ((displaySettings & 8) > 0) {
				if (isOutOfRange) {
					result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelOutOfRangeN", i + 1)));
				} else if (isNotFound) {
					result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelNotFoundN", i + 1)));
				} else {
					if ((displaySettings & 1) > 0) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelStorageN", i + 1, StringUtils.getFormatted("", energy, false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", energy, false)));
					}
					if ((displaySettings & 2) > 0) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelMaxStorageN", i + 1, StringUtils.getFormatted("", storage, false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", storage, false)));
					}
					if ((displaySettings & 4) > 0) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelOutputN", i + 1, StringUtils.getFormatted("", output, false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", output, false)));
					}
				}
			}
		}
		if (showSummary) {
			if ((displaySettings & 1) > 0) 
				result.add(new PanelString("msg.ec.InfoPanelStorage", totalEnergy, showLabels));
			if ((displaySettings & 2) > 0) 
				result.add(new PanelString("msg.ec.InfoPanelMaxStorage", totalStorage, showLabels));
			if ((displaySettings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput", totalOutput, showLabels));
		}
		return result;
	}

	@Override
	protected List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(5);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelStorage"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMaxStorage"), 2, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 4, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEach"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelTotal"), 16, damage));		
		return result;
	}
}