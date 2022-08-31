package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.utils.DataHelper;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemCardAdvGenerators extends ItemCardMain {

	@Override
	public CardState update(Level world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		BlockEntity te = world.getBlockEntity(target);
		CompoundTag tag = CrossModLoader.getCrossMod(ModIDs.ADV_GENERATORS).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(Level world, int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.hasField(DataHelper.HEAT) && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelHeat", reader.getDouble(DataHelper.HEAT), "HU", showLabels));
		if (reader.hasField(DataHelper.OUTPUT) && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUT), "FE/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTMB) && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTMB), "mB/t", showLabels));
		if (reader.hasField("steamOut") && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutputSteam", reader.getDouble("steamOut"), "mB/t", showLabels));
		if (reader.hasField("obsidianOut") && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutputObsidian", reader.getDouble("obsidianOut"), "mB/t", showLabels));
		if (reader.hasField(DataHelper.CONSUMPTIONMB) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble(DataHelper.CONSUMPTIONMB), "mB/t", showLabels));
		if (reader.hasField("carbonIn") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumptionCarbon", reader.getDouble("carbonIn"), "mB/t", showLabels));
		if (reader.hasField("steamIn") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumptionSteam", reader.getDouble("steamIn"), "mB/t", showLabels));
		if (reader.hasField("waterIn") && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumptionWater", reader.getDouble("waterIn"), "mB/t", showLabels));
		if (reader.hasField(DataHelper.ENERGY) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getDouble(DataHelper.ENERGY), "FE", showLabels));
		if (reader.hasField(DataHelper.CAPACITY) && (settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getDouble(DataHelper.CAPACITY), "FE", showLabels));
		if (reader.hasField("speed") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelSpeed", reader.getDouble("speed"), "RPM", showLabels));
		if (reader.hasField("turbines") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTurbines", reader.getInt("turbines"), showLabels));
		if (reader.hasField("heatingChambers") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelHeatingChambers", reader.getInt("heatingChambers"), showLabels));
		if (reader.hasField("mixingChambers") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMixingChambers", reader.getInt("mixingChambers"), showLabels));
		if (reader.hasField("carbon") && (settings & 32) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCarbon", reader.getDouble("carbon"), "mB", showLabels));
		if (reader.hasField(DataHelper.TANK) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK), showLabels));
		if (reader.hasField(DataHelper.TANK2) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK2), showLabels));
		if (reader.hasField(DataHelper.TANK3) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK3), showLabels));
		if (reader.hasField(DataHelper.TANK4) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK4), showLabels));
		if (reader.hasField(DataHelper.ACTIVE))
			addOnOff(result, isServer, reader.getBoolean(DataHelper.ACTIVE));
		return result;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(4);
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelOutput"), 1));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelUsing"), 2));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelEnergy"), 4));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelCapacity"), 8));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelTank"), 16));
		result.add(new PanelSetting(I18n.get("msg.ec.cbInfoPanelOther"), 32));
		return result;
	}
}
