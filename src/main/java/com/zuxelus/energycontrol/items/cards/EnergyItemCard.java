package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import team.reborn.energy.EnergyStorage;

public class EnergyItemCard extends MainCardItem {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		BlockEntity be = world.getBlockEntity(target);
		if (be == null)
			return CardState.NO_TARGET;

		CompoundTag tag = getEnergyData(be);
		if (tag != null && tag.contains("type")) {
			reader.setInt("type", tag.getInt("type"));
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			if (tag.getInt("type") == 12)
				reader.setString("euType", tag.getString("euType"));
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();

		double energy = reader.getDouble("storage");
		double storage = reader.getDouble("maxStorage");
		String euType = "";

		switch (reader.getInt("type")) {
		case 10:
			euType = "AE";
			break;
		case 11:
			euType = "gJ";
			break;
		case 12:
			euType = reader.getString("euType");
			break;
		default:
			euType = "EU";
			break;
		}
		if ((settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, energy, showLabels));
		if ((settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, storage, showLabels));
		if ((settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFree" + euType, storage - energy, showLabels));
		if ((settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPercentage", storage == 0 ? 100 : ((energy / storage) * 100), showLabels));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<PanelSetting>(4);
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelEnergy"), 1, ItemCardType.CARD_ENERGY));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelFree"), 2, ItemCardType.CARD_ENERGY));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelCapacity"), 4, ItemCardType.CARD_ENERGY));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelPercentage"), 8, ItemCardType.CARD_ENERGY));
		return result;
	}

	public CompoundTag getEnergyData(BlockEntity be) {
		if (be instanceof EnergyStorage) {
			CompoundTag tag = new CompoundTag();
			EnergyStorage storage = (EnergyStorage) be;
			tag.putInt("type", 12);
			tag.putString("euType", "E");
			tag.putDouble("storage", storage.getStored(null));
			tag.putDouble("maxStorage", storage.getMaxStoredPower());
			return tag;
		}
		return null;
	}

	@Override
	public int getCradType() {
		return ItemCardType.CARD_ENERGY;
	}
}
