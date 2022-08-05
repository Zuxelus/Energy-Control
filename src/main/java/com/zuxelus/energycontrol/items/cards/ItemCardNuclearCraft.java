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

public class ItemCardNuclearCraft extends ItemCardBase {
	private static DecimalFormat df = new DecimalFormat("0.0");

	public ItemCardNuclearCraft() {
		super(ItemCardType.CARD_NUCLEARCRAFT, "card_nuclearcraft");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.NUCLEAR_CRAFT).getCardData(world, target);
		if (tag == null)
			return CardState.NO_TARGET;
		reader.reset();
		reader.copyFrom(tag);
		return CardState.OK;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (!reader.hasField("type"))
			return result;
		
		int type = reader.getInt("type");
		switch (type) {
		case 1:
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getInt(DataHelper.ENERGY), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt(DataHelper.CAPACITY), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getInt("output"), "RF/t", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelRadiation", reader.getDouble("radiation"), showLabels));
			break;
		case 2:
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getInt(DataHelper.ENERGY), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt(DataHelper.CAPACITY), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelProcessPowerRF", reader.getInt("power"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSpeedMultiplierRF", reader.getDouble("speedM"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelPowerMultiplierRF", reader.getDouble("powerM"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelProcessTime", reader.getInt("time"), showLabels));
			break;
		case 3:
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getInt(DataHelper.ENERGY), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt(DataHelper.CAPACITY), "RF", showLabels));
			break;
		case 4:
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getInt(DataHelper.ENERGY), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt(DataHelper.CAPACITY), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelOutput", reader.getInt("output"), "RF/t", showLabels));
			break;
		case 5:
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getInt(DataHelper.ENERGY), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt(DataHelper.CAPACITY), "RF", showLabels));
			break;
		case 6:
			addHeat(result, "msg.ec.InfoPanelTemp", (int) Math.round(reader.getDouble("heat")), reader.getInt("maxHeat"), showLabels);
			result.add(new PanelString("msg.ec.InfoPanelHeatChange", reader.getDouble("heatChange"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getString("fuel"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelProcessPowerRF", reader.getDouble("power"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelEnergy", reader.getInt(DataHelper.ENERGY), "RF", showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacity", reader.getInt(DataHelper.CAPACITY), "RF", showLabels));
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
		case 8:
			result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
			result.add(new PanelString("msg.nc.NumberOfClusters", reader.getInt("clusterCount"), showLabels));
			result.add(new PanelString("msg.nc.MeanEfficiency", reader.getDouble("meanEfficiency"), showLabels));
			result.add(new PanelString("msg.nc.ProductionRate", reader.getDouble("outputRate"), "mB/t", showLabels));
			result.add(new PanelString("msg.nc.SparsityEfficiencyMult", reader.getDouble("sparsityEfficiencyMult"), showLabels));
			result.add(new PanelString("msg.nc.CasingTemp", reader.getInt("temp"), "K", showLabels));
			//result.add(new PanelString("msg.nc.RawHeating", reader.getLong("rawHeating"), "H/t", showLabels));
			result.add(new PanelString("msg.nc.NetClusterHeating", reader.getLong("heat"), "H/t", showLabels));
			result.add(new PanelString("msg.nc.CasingHeatLevel", reader.getString("level"), showLabels));
			result.add(new PanelString("msg.nc.TotalClusterCooling", reader.getLong("cooling"), "H/t", showLabels));
			addOnOff(result, isServer, reader.getBoolean("active"));
			break;
		case 9:
			result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
			result.add(new PanelString("msg.nc.NumberOfClusters", reader.getInt("clusterCount"), showLabels));
			result.add(new PanelString("msg.nc.MeanEfficiency", reader.getDouble("meanEfficiency"), showLabels));
			result.add(new PanelString("msg.nc.SparsityEfficiencyMult", reader.getDouble("sparsityEfficiencyMult"), showLabels));
			result.add(new PanelString("msg.nc.CasingTemp", reader.getInt("temp"), "K", showLabels));
			//result.add(new PanelString("msg.nc.RawHeating", reader.getLong("rawHeating"), "H/t", showLabels));
			result.add(new PanelString("msg.nc.NetClusterHeating", reader.getLong("heat"), "H/t", showLabels));
			result.add(new PanelString("msg.nc.CasingHeatLevel", reader.getString("level"), showLabels));
			result.add(new PanelString("msg.nc.TotalClusterCooling", reader.getLong("cooling"), "H/t", showLabels));
			addOnOff(result, isServer, reader.getBoolean("active"));
			break;
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<>(2);
		result.add(new PanelSetting(I18n.format("msg.ec.cbStatus"), 1));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 2));
		return result;
	}
}
