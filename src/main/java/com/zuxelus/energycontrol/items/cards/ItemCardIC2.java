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

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardIC2 extends ItemCardBase {
	public ItemCardIC2() {
		super(ItemCardType.CARD_IC2, "card_ic2");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.IC2).getCardData(world, target);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.hasField(DataHelper.HEAT) && (settings & 1) > 0)
			addHeat(result, "msg.ec.InfoPanelTemp", reader.getInt(DataHelper.HEAT), reader.getInt(DataHelper.MAXHEAT), showLabels);
		if (reader.hasField(DataHelper.MAXHEAT) && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMaxHeat", reader.getInt(DataHelper.MAXHEAT), showLabels));
		if (reader.hasField(DataHelper.MAXHEAT) && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMelting", reader.getInt(DataHelper.MAXHEAT) * 85 / 100, showLabels));
		if (reader.hasField("heatD") && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTemp", reader.getDouble("heatD"), "°C", showLabels));
		if (reader.hasField("heatChange") && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelHeatChange", reader.getInt("heatChange"), showLabels));
		if (reader.hasField(DataHelper.ENERGY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getLong(DataHelper.ENERGY), "EU", showLabels));
		if (reader.hasField(DataHelper.ENERGYHU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getLong(DataHelper.ENERGYHU), "HU", showLabels));
		if (reader.hasField(DataHelper.ENERGYKU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getLong(DataHelper.ENERGYKU), "KU", showLabels));
		if (reader.hasField(DataHelper.CAPACITY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getLong(DataHelper.CAPACITY), "EU", showLabels));
		if (reader.hasField(DataHelper.CAPACITYHU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getLong(DataHelper.CAPACITYHU), "HU", showLabels));
		if (reader.hasField(DataHelper.CAPACITYKU) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getLong(DataHelper.CAPACITYKU), "KU", showLabels));
		if (reader.hasField(DataHelper.DIFF) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelDifference", reader.getDouble(DataHelper.DIFF), "EU/t", showLabels));
		if (reader.hasField(DataHelper.CONSUMPTION) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble(DataHelper.CONSUMPTION), "mB/t", showLabels));
		if (reader.hasField("consumptionEU") && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble("consumptionEU"), "EU/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUT) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUT), "EU/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTHU) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTHU), "HU/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTKU) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTKU), "KU/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTMB) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTMB), "mB/t", showLabels));
		if (reader.hasField(DataHelper.MULTIPLIER) && (settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMultiplier", reader.getDouble(DataHelper.MULTIPLIER), showLabels));
		if (reader.hasField(DataHelper.TANK) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK), showLabels));
		if (reader.hasField(DataHelper.TANK2) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK2), showLabels));
		if (reader.hasField(DataHelper.FUEL) && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getInt(DataHelper.FUEL), showLabels));
		if (reader.hasField("pellets") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPellets", reader.getInt("pellets"), showLabels));
		if (reader.hasField("motors") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMotors", reader.getInt("motors"), showLabels));
		if (reader.hasField("coils") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCoils", reader.getInt("coils"), showLabels));
		if (reader.hasField("conductors") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConductors", reader.getInt("conductors"), showLabels));
		if (reader.hasField("wind") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelWindStrength", reader.getDouble("wind"), showLabels));
		if (reader.hasField("obstructedBlocks") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelObstructedBlocks", reader.getInt("obstructedBlocks"), showLabels));
		if (reader.hasField("waterFlow") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelWaterFlow", reader.getDouble("waterFlow"), showLabels));
		if (reader.hasField("height") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelHeight", reader.getInt("height"), showLabels));
		if (reader.hasField("health") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelRotorHealth", reader.getDouble("health"), showLabels));
		if (reader.hasField("progress") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelProgress", reader.getDouble("progress"), showLabels));
		if (reader.hasField("pressure") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPressure", reader.getInt("pressure"), "bar", showLabels));
		if (reader.hasField("calcification") && (settings & 64) > 0)
			result.add(new PanelString("ic2.SteamGenerator.gui.calcification", reader.getDouble("calcification"), "%", showLabels));
		if (reader.hasField("timeLeft") && (settings & 64) > 0) {
			int timeLeft = reader.getInt("timeLeft");
			int hours = timeLeft / 3600;
			int minutes = (timeLeft % 3600) / 60;
			int seconds = timeLeft % 60;
			result.add(new PanelString("msg.ec.InfoPanelTimeRemaining", String.format("%d:%02d:%02d", hours, minutes, seconds), showLabels));
		}
		if (reader.hasField(DataHelper.ACTIVE) && (settings & 32) > 0)
			addOnOff(result, isServer, reader.getBoolean(DataHelper.ACTIVE));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<PanelSetting>(7);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelHeat"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMultiplier"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelTank"), 16));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 32));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOther"), 64));
		return result;
	}
}