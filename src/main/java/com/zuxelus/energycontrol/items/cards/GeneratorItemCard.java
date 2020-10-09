package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import techreborn.blockentity.generator.SolarPanelBlockEntity;

public class GeneratorItemCard extends MainCardItem {

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		BlockEntity be = world.getBlockEntity(target);
		if (be == null)
			return CardState.NO_TARGET;

		CompoundTag tag = getEnergyData(be);
		if (tag == null || !tag.contains("type"))
			return CardState.NO_TARGET;

		reader.setInt("type", tag.getInt("type"));
		reader.setString("euType", tag.getString("euType"));
		switch (tag.getInt("type")) {
		case 1:
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setDouble("production", tag.getDouble("production"));
			break;
		/*case 2:
			reader.setDouble("production", tag.getDouble("production"));
			reader.setDouble("multiplier", tag.getDouble("multiplier"));
			break;
		case 3:
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setDouble("production", tag.getDouble("production"));
			reader.setDouble("multiplier", tag.getDouble("multiplier"));
			break;
		case 4:
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setInt("items", tag.getInteger("items"));
			reader.setDouble("production", tag.getDouble("production"));
			reader.setDouble("multiplier", tag.getDouble("multiplier"));
			break;
		case 5:
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setDouble("production", tag.getDouble("production"));
			reader.setInt("burnTime", tag.getInteger("burnTime"));
			break;
		case 6:
			reader.setString("status", tag.getString("status"));
			reader.setDouble("storage", tag.getDouble("storage"));
			reader.setDouble("maxStorage", tag.getDouble("maxStorage"));
			reader.setDouble("production", tag.getDouble("production"));
			reader.setDouble("multiplier", tag.getDouble("multiplier"));
			reader.setInt("progress", tag.getInteger("progress"));
			reader.setInt("coilCount", tag.getInteger("coilCount"));
			break;*/
		}
		reader.setBoolean("active", tag.getBoolean("active"));
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(World world, int settings, ICardReader reader, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		String euType = reader.getString("euType");
		switch (reader.getInt("type")) {
		case 1:
			if ((settings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("maxStorage"), showLabels));
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			break;
		/*case 2:
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getDouble("multiplier"), showLabels));
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			break;
		case 3:
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			if ((settings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("maxStorage"), showLabels));
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getDouble("multiplier"), showLabels));
			break;
		case 4:
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			if ((settings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("maxStorage"), showLabels));
			if ((settings & 16) > 0)
				result.add(new PanelString("msg.ec.InfoPanelPellets", reader.getInt("items"), showLabels));
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getDouble("multiplier"), showLabels));
			break;
		case 5:
			if ((settings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("maxStorage"), showLabels));
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			if ((settings & 64) > 0)
				result.add(new PanelString("msg.ec.InfoPanelBurnTime", reader.getInt("burnTime"), showLabels));
			break;
		case 6:
			result.add(new PanelString("msg.ec.InfoPanelStatus", reader.getString("status"), showLabels));
			if ((settings & 1) > 0)
				result.add(new PanelString("msg.ec.InfoPanelEnergy" + euType, reader.getDouble("storage"), showLabels));
			if ((settings & 2) > 0)
				result.add(new PanelString("msg.ec.InfoPanelCapacity" + euType, reader.getDouble("maxStorage"), showLabels));
			if ((settings & 8) > 0)
				result.add(new PanelString("msg.ec.InfoPanelOutput" + euType, reader.getDouble("production"), showLabels));
			if ((settings & 4) > 0)
				result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getDouble("multiplier"), showLabels));
			if ((settings & 64) > 0) {
				result.add(new PanelString("msg.ec.InfoPanelProgress", reader.getInt("progress"), showLabels));
				result.add(new PanelString("msg.ec.InfoPanelCoils", reader.getInt("coilCount"), showLabels));
			}
			break;*/
		}
		if ((settings & 32) > 0)
			addOnOff(result, reader.getBoolean("active"), world.isClient);
		return result;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<PanelSetting>(7);
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelEnergy"), 1, getCardType()));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelCapacity"), 2, getCardType()));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelMultiplier"), 4, getCardType()));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelOutput"), 8, getCardType()));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelItems"), 16, getCardType()));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelOnOff"), 32, getCardType()));
		result.add(new PanelSetting(I18n.translate("msg.ec.cbInfoPanelAdditionalInfo"), 64, getCardType()));
		return result;
	}

	public CompoundTag getEnergyData(BlockEntity be) {
		if (be instanceof SolarPanelBlockEntity) {
			CompoundTag tag = new CompoundTag();
			SolarPanelBlockEntity solar = (SolarPanelBlockEntity) be;
			tag.putInt("type", 1);
			tag.putString("euType", "E");
			boolean active = solar.powerChange != 0;
			tag.putBoolean("active", active);
			tag.putDouble("storage", solar.getEnergy());
			tag.putDouble("maxStorage", solar.getMaxStoredPower());
			if (active)
				tag.putDouble("production", solar.powerChange);
			else
				tag.putDouble("production", 0);
			return tag;
		}
		return null;
	}

	@Override
	public int getCardType() {
		return ItemCardType.CARD_GENERATOR;
	}
}