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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardGalacticraft extends ItemCardBase {

	public ItemCardGalacticraft() {
		super(ItemCardType.CARD_GALACTICRAFT, "card_galacticraft");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.GALACTICRAFT_PLANETS).getCardData(world, target);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (reader.hasField(DataHelper.STATUS) && (settings & 1) > 0)
			result.add(new PanelString("msg.ec.InfoPanelStatus", reader.getString(DataHelper.STATUS), showLabels));
		if (reader.hasField(DataHelper.ENERGY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getDouble(DataHelper.ENERGY), "gJ", showLabels));
		if (reader.hasField(DataHelper.CAPACITY) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getDouble(DataHelper.CAPACITY), "gJ", showLabels));
		if (reader.hasField(DataHelper.OUTPUT) && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUT), "gJ/t", showLabels));
		if (reader.hasField(DataHelper.DIFF) && (settings & 2) > 0)
			result.add(new PanelString("msg.ec.InfoPanelDifference", reader.getDouble(DataHelper.DIFF), "gJ/t", showLabels));
		if (reader.hasField("oxygenPerTick") && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCollecting", reader.getDouble("oxygenPerTick"), showLabels));
		if (reader.hasField("oxygenUse") && (settings & 4) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOxygenUse", reader.getDouble("oxygenUse"), showLabels));
		if (reader.hasField("thermalStatus") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelThermalControl", reader.getString("thermalStatus"), showLabels));
		if (reader.hasField("oxygenStored") && reader.hasField("oxygenCapacity") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOxygen", String.format("%s / %s mB", reader.getInt("oxygenStored"), reader.getInt("oxygenCapacity")), showLabels));
		if (reader.hasField("oilTank") && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOilTank", reader.getString("oilTank"), showLabels));
		if (reader.hasField("fuelTank") && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getString("fuelTank"), showLabels));
		if (reader.hasField("waterTank") && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelWater", reader.getString("waterTank"), showLabels));
		if (reader.hasField("oxygenTank") && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOxygen", reader.getString("oxygenTank"), showLabels));
		if (reader.hasField("hydrogenTank") && (settings & 16) > 0)
			result.add(new PanelString("msg.ec.InfoPanelHydrogen", reader.getString("hydrogenTank"), showLabels));
		if (reader.hasField("valve") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelAtmosphericValve", reader.getInt("valve"), showLabels));
		if (reader.hasField("dioxideTank") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelCarbonDioxide", reader.getString("dioxideTank"), showLabels));
		if (reader.hasField("methaneTank") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelMethane", reader.getString("methaneTank"), showLabels));
		if (reader.hasField("carbon") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFragmentedCarbon", reader.getInt("carbon"), showLabels));
		if (reader.hasField("gasTankName") && (settings & 16) > 0)
			result.add(new PanelString(I18n.format(reader.getString("gasTankName")) + ": " + reader.getString("gasTank")));
		if (reader.hasField("liquidTankName") && (settings & 16) > 0)
			result.add(new PanelString(I18n.format(reader.getString("liquidTankName")) + ": " + reader.getString("liquidTank")));
		if (reader.hasField("liquidTank2Name") && (settings & 16) > 0)
			result.add(new PanelString(I18n.format(reader.getString("liquidTank2Name")) + ": " + reader.getString("liquidTank2")));
		if (reader.hasField("oxygenDetected") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelOxygenDetected", reader.getString("oxygenDetected"), showLabels));
		if (reader.hasField("boost") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelEnvironmentalBoost", reader.getDouble("boost"), showLabels));
		if (reader.hasField("sunVisible") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelSunVisible", reader.getDouble("sunVisible"), showLabels));
		if (reader.hasField("frequency") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelFrequency", reader.getInt("frequency"), showLabels));
		if (reader.hasField("target") && (settings & 64) > 0)
			result.add(new PanelString("msg.ec.InfoPanelTarget", reader.getInt("target"), showLabels));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(2);
		result.add(new PanelSetting(I18n.format("msg.ec.cbStatus"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 2));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOutput"), 4));
		//result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelMultiplier"), 8));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelTank"), 16));
		//result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), 32));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOther"), 64));
		return result;
	}
}
