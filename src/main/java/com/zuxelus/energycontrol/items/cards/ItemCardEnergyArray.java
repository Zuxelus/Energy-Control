package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.StringUtils;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemCardEnergyArray extends ItemCardMain {
	private static final int STATUS_NOT_FOUND = Integer.MIN_VALUE;
	private static final int STATUS_OUT_OF_RANGE = Integer.MIN_VALUE + 1;

	@Override
	public CardState update(Level world, ICardReader reader, int range, BlockPos pos) {
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
				BlockEntity te = world.getBlockEntity(target);
				if (te != null) {
					CompoundTag tag = CrossModLoader.getEnergyData(te);
					if (tag != null) {
						double stored = tag.getDouble(DataHelper.ENERGY);
						double capacity = tag.getDouble(DataHelper.CAPACITY);
						totalEnergy += stored;
						reader.setInt(String.format("_%denergy", i), (int) stored);
						reader.setInt(String.format("_%dmaxStorage", i), (int) capacity);
						foundAny = true;
					} else
						reader.setInt(String.format("_%denergy", i), STATUS_NOT_FOUND);
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
	public List<PanelString> getStringData(Level world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		double totalEnergy = 0;
		double totalStorage = 0;
		boolean showEach = (settings & 1) > 0;
		boolean showSummary = (settings & 2) > 0;
		boolean showEnergy = (settings & 4) > 0;
		boolean showFree = (settings & 8) > 0;
		boolean showStorage = (settings & 16) > 0;
		boolean showPercentage = (settings & 32) > 0;
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
				result.add(new PanelString("msg.ec.InfoPanelCapacity", totalStorage, showLabels));
			if (showPercentage)
				result.add(new PanelString("msg.ec.InfoPanelPercentage",totalStorage == 0 ? 100 : ((totalEnergy / totalStorage) * 100), showLabels));
		}
		return result;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(6);
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelEachCard"), 1));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelEnergy"), 4));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelFree"), 8));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelStorage"), 16));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelPercentage"), 32));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelTotal"), 2));
		return result;
	}

	@Override
	public boolean isRemoteCard() {
		return false;
	}
}
