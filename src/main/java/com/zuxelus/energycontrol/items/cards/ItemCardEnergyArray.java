package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.EnergyCardHelper;
import com.zuxelus.energycontrol.crossmod.EnergyStorageData;
import com.zuxelus.energycontrol.utils.StringUtils;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardEnergyArray extends ItemCardBase {
	private static final int STATUS_NOT_FOUND = Integer.MIN_VALUE;
	private static final int STATUS_OUT_OF_RANGE = Integer.MIN_VALUE + 1;	
	
	public ItemCardEnergyArray() {
		super(ItemCardType.CARD_ENERGY_ARRAY, "card_energy_array");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
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
				EnergyStorageData storage = EnergyCardHelper.getStorageAt(world, target, reader.getInt(String.format("_%dtargetType", i)));
				if (storage != null) {
					double capacity = storage.values.get(0);
					double stored = storage.values.get(1);
					totalEnergy += stored;
					reader.setInt(String.format("_%denergy", i), (int) stored);
					reader.setInt(String.format("_%dmaxStorage", i), (int) capacity);
					foundAny = true;
				} else 
					reader.setInt(String.format("_%denergy", i), STATUS_NOT_FOUND);
			} else {
				reader.setInt(String.format("_%denergy", i), STATUS_OUT_OF_RANGE);
				outOfRange = true;
			}
		}
		reader.setDouble("energy", totalEnergy);
		if (!foundAny) {
			if (outOfRange)
				return CardState.OUT_OF_RANGE;
			return CardState.NO_TARGET;
		}
		return CardState.OK;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelString> getStringData(int displaySettings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		double totalEnergy = 0;
		double totalStorage = 0;
		boolean showEach = (displaySettings & 1) > 0;
		boolean showSummary = (displaySettings & 2) > 0;
		boolean showEnergy = (displaySettings & 4) > 0;
		boolean showFree = (displaySettings & 8) > 0;
		boolean showStorage = (displaySettings & 16) > 0;
		boolean showPercentage = (displaySettings & 32) > 0;
		for (int i = 0; i < reader.getCardCount(); i++) {
			int energy = reader.getInt(String.format("_%denergy", i));
			int storage = reader.getInt(String.format("_%dmaxStorage", i));
			boolean isOutOfRange = energy == STATUS_OUT_OF_RANGE;
			boolean isNotFound = energy == STATUS_NOT_FOUND;
			if (showSummary && !isOutOfRange && !isNotFound) {
				totalEnergy += energy;
				totalStorage += storage;
			}

			if (showEach) {
				if (isOutOfRange) {
					result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelOutOfRangeN", i + 1)));
				} else if (isNotFound) {
					result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelNotFoundN", i + 1)));
				} else {
					if (showEnergy) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelEnergyN", i + 1, StringUtils.getFormatted("", energy, false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", energy, false)));
					}
					if (showFree) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelFreeN", i + 1, StringUtils.getFormatted("", storage - energy, false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", storage - energy, false)));
					}
					if (showStorage) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelStorageN", i + 1, StringUtils.getFormatted("", storage, false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", storage, false)));
					}
					if (showPercentage) {
						if (showLabels)
							result.add(new PanelString(StringUtils.getFormattedKey("msg.ec.InfoPanelPercentageN", i + 1, StringUtils.getFormatted("", storage == 0 ? 100 : (int)(((double)energy / storage) * 100D), false))));
						else
							result.add(new PanelString(StringUtils.getFormatted("", storage == 0 ? 100 : (int)(((double)energy / storage) * 100D), false)));
					}
				}
			}
		}
		if (showSummary) {
			if (showEnergy) 
				result.add(new PanelString("msg.ec.InfoPanelEnergy", totalEnergy, showLabels));
			if (showFree) 
				result.add(new PanelString("msg.ec.InfoPanelFree", totalStorage - totalEnergy, showLabels));
			if (showStorage)
				result.add(new PanelString("msg.ec.InfoPanelStorage", totalStorage, showLabels));
			if (showPercentage) 
				result.add(new PanelString("msg.ec.InfoPanelPercentage",totalStorage == 0 ? 100 : ((totalEnergy / totalStorage) * 100), showLabels));
		}
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(6);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEach"), 1,damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 4,damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelFree"), 8, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelStorage"), 16, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelPercentage"), 32, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelTotal"), 2,damage));
		return result;
	}

	@Override
	public boolean isRemoteCard() {
		return false;
	}
}