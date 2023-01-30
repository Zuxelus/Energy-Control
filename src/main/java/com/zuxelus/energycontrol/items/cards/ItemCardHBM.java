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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardHBM extends ItemCardBase {
	private static DecimalFormat df = new DecimalFormat("0.0");

	public ItemCardHBM() {
		super(ItemCardType.CARD_HBM, "card_hbm");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.HBM).getCardData(world, target);
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
		if (reader.hasField(DataHelper.CAPACITY))
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getLong(DataHelper.CAPACITY), "HE", showLabels));
		if (reader.hasField(DataHelper.CONSUMPTION))
			result.add(new PanelString("msg.ec.InfoPanelConsumption", reader.getDouble(DataHelper.CONSUMPTION), "mB/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUT))
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUT), "HE/t", showLabels));
		if (reader.hasField(DataHelper.OUTPUTMB))
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getDouble(DataHelper.OUTPUTMB), "mB/t", showLabels));
		if (reader.hasField(DataHelper.DIFF))
			result.add(new PanelString("msg.ec.InfoPanelDifference", reader.getLong(DataHelper.DIFF), "HE/t", showLabels));
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
		if (reader.hasField("heat"))
			result.add(new PanelString("msg.ec.InfoPanelHeat", reader.getLong("heat"), showLabels));
		if (reader.hasField("heatD"))
			result.add(new PanelString("msg.ec.InfoPanelHeat", reader.getDouble("heatD"), "°C", showLabels));
		if (reader.hasField("fuel"))
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getInt("fuel"), showLabels));
		if (reader.hasField("fuelText"))
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getString("fuelText"), showLabels));
		if (reader.hasField("depleted"))
			result.add(new PanelString("msg.ec.InfoPanelDepleted", reader.getString("depleted"), showLabels));
		if (reader.hasField("fluxFast"))
			result.add(new PanelString("msg.ec.InfoPanelFluxFast", reader.getDouble("fluxFast"), showLabels));
		if (reader.hasField("fluxSlow"))
			result.add(new PanelString("msg.ec.InfoPanelFluxSlow", reader.getDouble("fluxSlow"), showLabels));
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
		if (reader.hasField("chunkRad"))
			if (showLabels)
				result.add(new PanelString(net.minecraft.util.text.translation.I18n.translateToLocalFormatted("geiger.chunkRad") +
						" " + reader.getString("chunkRad")));
			else
				result.add(new PanelString(reader.getString("chunkRad")));
		if (reader.hasField(DataHelper.ACTIVE))
			addOnOff(result, isServer, reader.getBoolean(DataHelper.ACTIVE));
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<>(2);
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelStatus"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 2));
		return result;
	}
}
