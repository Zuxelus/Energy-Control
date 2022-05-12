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
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemCardHBM extends ItemCardBase {
	private static DecimalFormat df = new DecimalFormat("0.0");

	public ItemCardHBM() {
		super(ItemCardType.CARD_HBM, "card_hbm");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.HBM).getCardData(te);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.hasField(DataHelper.ENERGY))
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getLong(DataHelper.ENERGY), "HE", showLabels));
		if (reader.hasField("energy_"))
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getLong("energy_"), showLabels));
		if (reader.hasField(DataHelper.CAPACITY))
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getLong(DataHelper.CAPACITY), "HE", showLabels));
		if (reader.hasField("capacity_"))
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getLong("capacity_"), showLabels));
		if (reader.hasField("consumptionHE"))
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble("consumptionHE"), "HE/t", showLabels));
		if (reader.hasField(DataHelper.CONSUMPTION))
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble(DataHelper.CONSUMPTION), "mB/t", showLabels));
		if (reader.hasField("consumption_"))
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getString("consumption_") + " mB/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUT))
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUT), "HE/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTMB))
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTMB), "mB/t", showLabels));
		if (reader.hasField("diff"))
			result.add(new PanelString("msg.ec.InfoPanelDifference", reader.getLong("diff"), "HE/t", showLabels));
		if (reader.hasField("temp"))
			result.add(new PanelString("msg.ec.InfoPanelTemperature", reader.getInt("temp"), "K", showLabels));
		if (reader.hasField("speed"))
			result.add(new PanelString("msg.ec.InfoPanelRotorSpeed", reader.getInt("speed"), showLabels));
		if (reader.hasField("core"))
			result.add(new PanelString("msg.ec.InfoPanelCoreHeat", reader.getLong("core"), "°C", showLabels));
		if (reader.hasField("hull"))
			result.add(new PanelString("msg.ec.InfoPanelHullHeat", reader.getLong("hull"), "°C", showLabels));
		if (reader.hasField("level"))
			result.add(new PanelString("msg.ec.InfoPanelOperatingLevel", reader.getString("level"), showLabels));
		if (reader.hasField("heatL"))
			result.add(new PanelString("msg.ec.InfoPanelTemp", reader.getLong("heatL"), showLabels));
		if (reader.hasField(DataHelper.HEAT))
			result.add(new PanelString("msg.ec.InfoPanelTemp", reader.getDouble("heat"), "°C", showLabels));
		if (reader.hasField("pressure"))
			result.add(new PanelString("msg.ec.InfoPanelPressure", reader.getLong("pressure"), "bar", showLabels));
		if (reader.hasField("fuel"))
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getInt("fuel"), showLabels));
		if (reader.hasField("fuelText"))
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getString("fuelText"), showLabels));
		if (reader.hasField("depleted"))
			result.add(new PanelString("msg.ec.InfoPanelDepleted", reader.getString("depleted"), showLabels));
		if (reader.hasField("depletion"))
			result.add(new PanelString("rbmk.rod.depletion", reader.getDouble("depletion"), "%", showLabels));
		if (reader.hasField("xenon"))
			result.add(new PanelString("rbmk.rod.xenon", reader.getDouble("xenon"), "%", showLabels));
		if (reader.hasField("skin"))
			result.add(new PanelString("trait.rbmk.skinTemp", reader.getDouble("skin"), "°C", showLabels));
		if (reader.hasField("c_heat"))
			result.add(new PanelString("trait.rbmk.coreTemp", reader.getDouble("c_heat"), "°C", showLabels));
		if (reader.hasField("melt"))
			result.add(new PanelString("trait.rbmk.melt", reader.getDouble("melt"), "°C", showLabels));
		if (reader.hasField("progress"))
			result.add(new PanelString("msg.ec.InfoPanelProgress", reader.getInt("progress"), showLabels));
		if (reader.hasField("flux"))
			result.add(new PanelString("msg.ec.InfoPanelFlux", reader.getInt("flux"), showLabels));
		if (reader.hasField("water"))
			result.add(new PanelString("msg.ec.InfoPanelWater", reader.getInt("water"), showLabels));
		if (reader.hasField("durability"))
			result.add(new PanelString("msg.ec.InfoPanelLensDurability", reader.getLong("durability"), showLabels));
		if (reader.hasField(DataHelper.TANK))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK), showLabels));
		if (reader.hasField(DataHelper.TANK2))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK2), showLabels));
		if (reader.hasField(DataHelper.TANK3))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK3), showLabels));
		if (reader.hasField(DataHelper.TANK4))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK4), showLabels));
		if (reader.hasField(DataHelper.TANK5))
			result.add(new PanelString("msg.ec.InfoPanelTank", reader.getString(DataHelper.TANK5), showLabels));
		if (reader.hasField("pistons"))
			result.add(new PanelString("msg.ec.InfoPanelPistons", reader.getInt("pistons"), showLabels));
		if (reader.hasField("chunkRad"))
			if (showLabels)
				result.add(new PanelString(StatCollector.translateToLocalFormatted("geiger.chunkRad") +
						" " + reader.getString("chunkRad")));
			else
				result.add(new PanelString(reader.getString("chunkRad")));
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
