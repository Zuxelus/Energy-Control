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
import com.zuxelus.energycontrol.utils.DataHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardBigReactors extends ItemCardBase {
	private static DecimalFormat df = new DecimalFormat("0.0");

	public ItemCardBigReactors() {
		super(ItemCardType.CARD_BIG_REACTORS, "card_big_reactors");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.BIG_REACTORS).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.hasField("fuelHeat") && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCoreHeat", reader.getInt("fuelHeat"), showLabels));
		if (reader.hasField(DataHelper.HEAT) && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCasingHeat", reader.getInt(DataHelper.HEAT), showLabels));
		if (reader.hasField("cooling") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelPassiveCooling", reader.getBoolean("cooling").toString(), showLabels));
		if (reader.hasField(DataHelper.ENERGY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getDouble(DataHelper.ENERGY), "RF", showLabels));
		if (reader.hasField(DataHelper.CAPACITY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getDouble(DataHelper.CAPACITY), "RF", showLabels));
		if (reader.hasField(DataHelper.OUTPUT) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUT), "RF/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTMB) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTMB), "mB/t", showLabels));
		if (reader.hasField("fuel") && (settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getInt("fuel"), "mB", showLabels));
		if (reader.hasField("waste") && (settings & 8) > 0)
			result.add(new PanelString("msg.ec.InfoPanelWastemb", reader.getInt("waste"), showLabels));
		if ((settings & 64) > 0) {
			if (reader.hasField("fuelCapacity"))
				result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt("fuelCapacity"), "mB", showLabels));
			if (reader.hasField("consumption"))
				result.add(new PanelString("msg.ec.InfoPanelBurnupRatemb", reader.getDouble("consumption"), showLabels));
			if (reader.hasField("rods"))
				result.add(new PanelString("msg.ec.InfoPanelFuelRods", reader.getDouble("rods"), showLabels));
			if (reader.hasField("speed"))
				result.add(new PanelString("msg.ec.InfoPanelRotorSpeed", reader.getDouble("speed"), showLabels));
			if (reader.hasField("speedMax"))
				result.add(new PanelString("msg.ec.InfoPanelMaxSpeed", reader.getDouble("speedMax"), showLabels));
			if (reader.hasField("efficiency"))
				result.add(new PanelString("msg.ec.InfoPanelRotorEfficiency", reader.getDouble("efficiency"), showLabels));
			if (reader.hasField("blades"))
				result.add(new PanelString("msg.ec.InfoPanelBlades", reader.getInt("blades"), showLabels));
			if (reader.hasField("mass"))
				result.add(new PanelString("msg.ec.InfoPanelRotorMass", reader.getInt("mass"), showLabels));
			if (reader.hasField("size"))
				result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
		}
		if (reader.hasField(DataHelper.ACTIVE) && (settings & 32) > 0)
			addOnOff(result, isServer, reader.getBoolean(DataHelper.ACTIVE));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(6);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelHeat"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 4));
		result.add(new PanelSetting(I18n.format("msg.ec.cbFuel"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelTank"), 16));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 32));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOther"), 64));
		return result;
	}
}