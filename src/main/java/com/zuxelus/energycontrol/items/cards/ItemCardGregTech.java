package com.zuxelus.energycontrol.items.cards;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardGregTech extends ItemCardBase {
	private static DecimalFormat df = new DecimalFormat("0.0");

	public ItemCardGregTech() {
		super(ItemCardType.CARD_GREGTECH, "card_gregtech");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.GREGTECH).getCardData(world, target);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.hasField("stored"))
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getLong("stored"), "HE", showLabels));
		if (reader.hasField("capacity"))
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getLong("capacity"), "HE", showLabels));
		if (reader.hasField("consumption"))
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble("consumption"), "mB/s", showLabels));
		if (reader.hasField("output"))
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("output"), "HE/t", showLabels));
		if (reader.hasField("outputmb"))
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble("outputmb"), "mB/s", showLabels));
		/*if (reader.hasField("temp"))
			result.add(new PanelString("msg.ec.InfoPanelTemperature", reader.getInt("temp"), "K", showLabels));
		if (reader.hasField("speed"))
			result.add(new PanelString("msg.ec.InfoPanelRotorSpeed", reader.getInt("speed"), showLabels));
		if (reader.hasField("core"))
			result.add(new PanelString("msg.ec.InfoPanelCoreHeat", reader.getLong("core"), "°C", showLabels));
		if (reader.hasField("hull"))
			result.add(new PanelString("msg.ec.InfoPanelHullHeat", reader.getLong("hull"), "°C", showLabels));
		if (reader.hasField("level"))
			result.add(new PanelString("msg.ec.InfoPanelOperatingLevel", reader.getString("level"), showLabels));*/
		if (reader.hasField("heat"))
			result.add(new PanelString("msg.ec.InfoPanelHeat", reader.getLong("heat"), "°C", showLabels));
		/*if (reader.hasField("heatD"))
			result.add(new PanelString("msg.ec.InfoPanelHeat", reader.getDouble("heatD"), "°C", showLabels));
		if (reader.hasField("fuel"))
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getInt("fuel"), showLabels));
		if (reader.hasField("fuelText"))
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getString("fuelText"), showLabels));
		if (reader.hasField("depleted"))
			result.add(new PanelString("msg.ec.InfoPanelDepleted", reader.getString("depleted"), showLabels));*/
		if (reader.hasField("burnTime"))
			result.add(new PanelString("msg.ec.InfoPanelBurnTime", reader.getDouble("burnTime"), "s", showLabels));
		if (reader.hasField("tank"))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString("tank"), showLabels));
		if (reader.hasField("tank2"))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString("tank2"), showLabels));
		if (reader.hasField("tank3"))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString("tank3"), showLabels));
		if (reader.hasField("tank4"))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString("tank4"), showLabels));
		/*if (reader.hasField("tank5"))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString("tank5"), showLabels));
		if (reader.hasField("chunkRad"))
			if (showLabels)
				result.add(new PanelString(net.minecraft.util.text.translation.I18n.translateToLocalFormatted("geiger.chunkRad") +
						" " + reader.getString("chunkRad")));
			else
				result.add(new PanelString(reader.getString("chunkRad")));*/
		if (reader.hasField("active"))
			addOnOff(result, isServer, reader.getBoolean("active"));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(2);
		result.add(new PanelSetting(I18n.format("msg.ec.cbStatus"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 2));
		return result;
	}
}
