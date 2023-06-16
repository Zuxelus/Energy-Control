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

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemCardNuclearCraft extends ItemCardBase {
	private static DecimalFormat df = new DecimalFormat("0.0");

	public ItemCardNuclearCraft() {
		super(ItemCardType.CARD_NUCLEARCRAFT, "card_nuclearcraft");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.NUCLEAR_CRAFT).getCardData(te);
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
			addHeat(result, "msg.ec.InfoPanelHeat", reader.getInt(DataHelper.HEAT), reader.getInt(DataHelper.MAXHEAT), showLabels);
		if (reader.hasField("heatChange") && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelHeatChange", reader.getInt("heatChange"), showLabels));
		if (reader.hasField(DataHelper.ENERGY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getLong(DataHelper.ENERGY), "RF", showLabels));
		if (reader.hasField(DataHelper.CAPACITY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getLong(DataHelper.CAPACITY), "RF", showLabels));
		if (reader.hasField(DataHelper.DIFF) && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelDifference", reader.getLong(DataHelper.DIFF), "RF/t", showLabels));
		if (reader.hasField(DataHelper.CONSUMPTION) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble(DataHelper.CONSUMPTION), "RF/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUT) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUT), "RF/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTMB) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTMB), "mB/t", showLabels));
		if (reader.hasField(DataHelper.TANK) && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK), showLabels));
		if (reader.hasField(DataHelper.FUEL))
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getInt(DataHelper.FUEL), showLabels));
		if (reader.hasField("level1"))
			result.add(new PanelString(reader.getString("level1")));
		if (reader.hasField("level2"))
			result.add(new PanelString(reader.getString("level2")));
		if (reader.hasField("fuelp"))
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getDouble("fuelp"), "%", showLabels));

		if (reader.hasField("HOut") && (settings & 4) > 0)
			result.add(new PanelString("Hydrogen: %s", reader.getDouble("HOut"), "mB", showLabels));
		if (reader.hasField("DOut") && (settings & 4) > 0)
			result.add(new PanelString("Deuterium: %s", reader.getDouble("DOut"), "mB", showLabels));
		if (reader.hasField("TOut") && (settings & 4) > 0)
			result.add(new PanelString("Tritium: %s", reader.getDouble("TOut"), "mB", showLabels));
		if (reader.hasField("HE3Out") && (settings & 4) > 0)
			result.add(new PanelString("Helium-3: %s", reader.getDouble("HE3Out"), "mB", showLabels));
		if (reader.hasField("HE4Out") && (settings & 4) > 0)
			result.add(new PanelString("Helium-4: %s", reader.getDouble("HE4Out"), "mB", showLabels));
		if (reader.hasField("nOut") && (settings & 4) > 0)
			result.add(new PanelString("Neutron: %s", reader.getDouble("nOut"), "mB", showLabels));
		if (reader.hasField("efficiency"))
			result.add(new PanelString("msg.ec.InfoPanelEfficiency", reader.getDouble("efficiency"), "%", showLabels));
		if (reader.hasField("size") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
		if (reader.hasField("cells") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCells", reader.getInt("cells"), showLabels));
		
		
		
		
		if (reader.hasField(DataHelper.ACTIVE))
			addOnOff(result, isServer, reader.getBoolean(DataHelper.ACTIVE));
		/*if (!reader.hasField("type"))
			return result;
		
		int type = reader.getInt("type");
		switch (type) {
		case 1:
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getInt("stored"), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt("capacity"), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getInt("output"), "RF/t", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelRadiation", reader.getDouble("radiation"), showLabels));
			break;
		case 2:
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getInt("stored"), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt("capacity"), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelProcessPowerRF", reader.getInt("power"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSpeedMultiplierRF", reader.getDouble("speedM"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelPowerMultiplierRF", reader.getDouble("powerM"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelProcessTime", reader.getInt("time"), showLabels));
			break;
		case 3:
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getInt("stored"), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt("capacity"), "RF", showLabels));
			break;
		case 4:
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getInt("stored"), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt("capacity"), "RF", showLabels));
			//result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getInt("output"), "RF/t", showLabels));
			break;
		case 5:
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getInt("stored"), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt("capacity"), "RF", showLabels));
			break;
		case 6:
			addHeat(result, (int) Math.round(reader.getDouble("heat")), reader.getInt("maxHeat"), showLabels);
			result.add(new PanelString("msg.ec.InfoPanelHeatChange", reader.getDouble("heatChange"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getString("fuel"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelProcessPowerRF", reader.getDouble("power"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getInt("stored"), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt("capacity"), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCoolingRate", reader.getDouble("cooling"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCells", reader.getInt("cells"), showLabels));
			addOnOff(result, isServer, reader.getBoolean("active"));
			break;
		case 7:
			if (!reader.getString("gasTankName").equals(""))
				result.add(new PanelString(I18n.format(reader.getString("gasTankName")) + ": " + reader.getString("gasTank")));
			result.add(new PanelString("msg.ec.InfoPanelAtmosphericValve", reader.getInt("valve"), showLabels));
			if (!reader.getString("liquidTankName").equals(""))
				result.add(new PanelString(I18n.format(reader.getString("liquidTankName")) + ": " + reader.getString("liquidTank")));
			if (!reader.getString("liquidTank2Name").equals(""))
				result.add(new PanelString(I18n.format(reader.getString("liquidTank2Name")) + ": " + reader.getString("liquidTank2")));
			break;
		case 10:
			result.add(new PanelString("msg.ec.InfoPanelOutputgJ", reader.getInt("production"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelEnvironmentalBoost", reader.getDouble("boost"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSunVisible", reader.getDouble("sunVisible"), showLabels));
			break;
		case 11:
			result.add(new PanelString("msg.ec.InfoPanelFrequency", reader.getInt("frequency"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelTarget", reader.getInt("target"), showLabels));
			break;
		case 12:
			result.add(new PanelString("msg.ec.InfoPanelOutputgJ", reader.getInt("production"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelEnvironmentalBoost", reader.getDouble("boost"), showLabels));
			break;
		}*/
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<PanelSetting>(2);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelStatus"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 2));
		return result;
	}
}
