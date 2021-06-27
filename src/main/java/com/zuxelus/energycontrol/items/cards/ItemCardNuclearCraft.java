package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import nc.tile.generator.TileDecayGenerator;
import nc.tile.generator.TileFissionController;
import nc.tile.generator.TileSolarPanel;
import nc.tile.processor.TileFluidProcessor;
import nc.tile.processor.TileItemProcessor;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

		TileEntity te = world.getTileEntity(target);
		if (te instanceof TileDecayGenerator) {
			reader.setInt("type", 1);
			reader.setInt("stored", ((TileDecayGenerator) te).getEnergyStored());
			reader.setInt("capacity", ((TileDecayGenerator) te).getMaxEnergyStored());
			reader.setInt("output", ((TileDecayGenerator) te).getGenerated());
			reader.setDouble("radiation", ((TileDecayGenerator) te).getRadiation());
			return CardState.OK;
		}
		if (te instanceof TileItemProcessor) {
			reader.setInt("type", 2);
			reader.setInt("stored", ((TileItemProcessor) te).getEnergyStored());
			reader.setInt("capacity", ((TileItemProcessor) te).getMaxEnergyStored());
			reader.setInt("power", ((TileItemProcessor) te).getProcessPower());
			reader.setDouble("speedM", ((TileItemProcessor) te).getSpeedMultiplier());
			reader.setDouble("powerM", ((TileItemProcessor) te).getPowerMultiplier());
			reader.setInt("time", ((TileItemProcessor) te).getProcessTime());
			return CardState.OK;
		}
		if (te instanceof TileFluidProcessor) {
			reader.setInt("type", 2);
			reader.setInt("stored", ((TileFluidProcessor) te).getEnergyStored());
			reader.setInt("capacity", ((TileFluidProcessor) te).getMaxEnergyStored());
			reader.setInt("power", ((TileFluidProcessor) te).getProcessPower());
			reader.setDouble("speedM", ((TileFluidProcessor) te).getSpeedMultiplier());
			reader.setDouble("powerM", ((TileFluidProcessor) te).getPowerMultiplier());
			reader.setInt("time", ((TileFluidProcessor) te).getProcessTime());
			return CardState.OK;
		}
		if (te instanceof TileSolarPanel) {
			reader.setInt("type", 4);
			reader.setInt("stored", ((TileSolarPanel) te).getEnergyStored());
			reader.setInt("capacity", ((TileSolarPanel) te).getMaxEnergyStored());
			reader.setInt("output", ((TileSolarPanel) te).getGenerated());
			return CardState.OK;
		}
		if (te instanceof TileFissionController) {
			reader.setInt("type", 6);
			TileFissionController reactor = (TileFissionController) te;
			reader.setBoolean("active", reactor.isProcessing);
			reader.setString("size", reactor.getLengthX() + "*" + reactor.getLengthY() + "*" + reactor.getLengthZ());
			reader.setString("fuel", reactor.getFuelName());
			reader.setInt("stored", reactor.getEnergyStored());
			reader.setInt("capacity", reactor.getMaxEnergyStored());
			reader.setDouble("efficiency", reactor.efficiency);
			reader.setDouble("heat", reactor.heat);
			reader.setInt("maxHeat", reactor.getMaxHeat());
			reader.setDouble("heatChange", reactor.heatChange);
			reader.setDouble("cooling", reactor.cooling);
			reader.setDouble("heatMult", reactor.heatMult);
			reader.setDouble("power", reactor.processPower);
			reader.setInt("cells", reactor.cells);
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		if (!reader.hasField("type"))
			return result;
		
		int type = reader.getInt("type");
		switch (type) {
		case 1:
			result.add(new PanelString("msg.ec.InfoPanelEnergyRF", reader.getInt("stored"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacityRF", reader.getInt("capacity"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelOutputRF", reader.getInt("output"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelRadiation", reader.getDouble("radiation"), showLabels));
			break;
		case 2:
			result.add(new PanelString("msg.ec.InfoPanelEnergyRF", reader.getInt("stored"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacityRF", reader.getInt("capacity"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelProcessPowerRF", reader.getInt("power"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSpeedMultiplierRF", reader.getDouble("speedM"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelPowerMultiplierRF", reader.getDouble("powerM"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelProcessTime", reader.getInt("time"), showLabels));
			break;
		case 3:
			result.add(new PanelString("msg.ec.InfoPanelEnergyRF", reader.getInt("stored"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacityRF", reader.getInt("capacity"), showLabels));
			break;
		case 4:
			result.add(new PanelString("msg.ec.InfoPanelEnergyRF", reader.getInt("stored"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacityRF", reader.getInt("capacity"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelOutputRF", reader.getInt("output"), showLabels));
			break;
		case 5:
			result.add(new PanelString("msg.ec.InfoPanelEnergyRF", reader.getInt("stored"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacityRF", reader.getInt("capacity"), showLabels));
			break;
		case 6:
			addHeat(result, (int) Math.round(reader.getDouble("heat")), reader.getInt("maxHeat"), showLabels);
			result.add(new PanelString("msg.ec.InfoPanelHeatChange", reader.getDouble("heatChange"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelSize", reader.getString("size"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelFuel", reader.getString("fuel"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelProcessPowerRF", reader.getDouble("power"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelEnergyRF", reader.getInt("stored"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCapacityRF", reader.getInt("capacity"), showLabels));
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
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		List<PanelSetting> result = new ArrayList<>(2);
		result.add(new PanelSetting(I18n.format("msg.ec.cbStatus"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 2, damage));
		return result;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_NUCLEARCRAFT;
	}
}
