package com.zuxelus.energycontrol.items.cards;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.crossmod.GalacticraftHelper;

import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenDetector;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemAtmosphericValve;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntitySolarArrayController;
import nc.tile.generator.TileDecayGenerator;
import nc.tile.generator.TileSolarPanel;
import nc.tile.processor.TileItemProcessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

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
		if (te instanceof TileSolarPanel) {
			reader.setInt("type", 3);
			reader.setInt("stored", ((TileSolarPanel) te).getEnergyStored());
			reader.setInt("capacity", ((TileSolarPanel) te).getMaxEnergyStored());
			reader.setInt("output", ((TileSolarPanel) te).getGenerated());
			return CardState.OK;
		}
		/*if (te instanceof TileEntityRefinery) {
			TileEntityRefinery ref = (TileEntityRefinery) te;
			reader.setInt("type", 4);
			reader.setString("status", GalacticraftHelper.getStatus(ref));
			reader.setDouble("stored", (double) ref.storage.getEnergyStoredGC());
			reader.setDouble("capacity", (double) ref.storage.getCapacityGC());
			reader.setString("oilTank", String.format("%s / %s mB", ref.oilTank.getFluidAmount(), ref.oilTank.getCapacity()));
			reader.setString("fuelTank", String.format("%s / %s mB", ref.fuelTank.getFluidAmount(), ref.fuelTank.getCapacity()));
			return CardState.OK;
		}
		if (te instanceof TileEntityElectrolyzer) {
			TileEntityElectrolyzer el = (TileEntityElectrolyzer) te;
			reader.setInt("type", 5);
			reader.setString("status", GalacticraftHelper.getStatus(el));
			reader.setDouble("stored", (double) el.storage.getEnergyStoredGC());
			reader.setDouble("capacity", (double) el.storage.getCapacityGC());
			reader.setString("waterTank", String.format("%s / %s mB", el.waterTank.getFluidAmount(), el.waterTank.getCapacity()));
			reader.setString("gasTank1", String.format("%s / %s mB", el.liquidTank.getFluidAmount(), el.liquidTank.getCapacity()));
			reader.setString("gasTank2", String.format("%s / %s mB", el.liquidTank2.getFluidAmount(), el.liquidTank2.getCapacity()));
			return CardState.OK;
		}
		if (te instanceof TileEntityMethaneSynthesizer) {
			TileEntityMethaneSynthesizer meth = (TileEntityMethaneSynthesizer) te;
			reader.setInt("type", 6);
			reader.setString("status", GalacticraftHelper.getStatus(meth));
			reader.setDouble("stored", (double) meth.storage.getEnergyStoredGC());
			reader.setDouble("capacity", (double) meth.storage.getCapacityGC());
			reader.setString("methaneTank", String.format("%s / %s mB", meth.liquidTank.getFluidAmount(), meth.liquidTank.getCapacity()));
			reader.setString("gasTank1", String.format("%s / %s mB", meth.gasTank.getFluidAmount(), meth.gasTank.getCapacity()));
			reader.setString("gasTank2", String.format("%s / %s mB", meth.gasTank2.getFluidAmount(), meth.gasTank2.getCapacity()));
			int counter = 0;
			ItemStack stack = meth.inventory.get(2);
			if (!stack.isEmpty() && stack.getItem() instanceof ItemAtmosphericValve)
				counter = stack.getCount();
			reader.setInt("valve", counter);
			counter = 0;
			stack = meth.inventory.get(3);
			if (!stack.isEmpty())
				counter = stack.getCount();
			reader.setInt("items", counter);
			return CardState.OK;
		}
		if (te instanceof TileEntityGasLiquefier) {
			TileEntityGasLiquefier gas = (TileEntityGasLiquefier) te;
			reader.setInt("type", 7);
			reader.setString("status", GalacticraftHelper.getStatus(gas));
			reader.setDouble("stored", (double) gas.storage.getEnergyStoredGC());
			reader.setDouble("capacity", (double) gas.storage.getCapacityGC());
			reader.setString("gasTank", String.format("%s / %s mB", gas.gasTank.getFluidAmount(), gas.gasTank.getCapacity()));
			FluidStack liquid = gas.gasTank.getFluid(); 
			if (liquid != null && liquid.getFluid() != null)
				reader.setString("gasTankName", liquid.getFluid().getUnlocalizedName());
			else
				reader.setString("gasTankName", "");
			reader.setString("liquidTank", String.format("%s / %s mB", gas.liquidTank.getFluidAmount(), gas.liquidTank.getCapacity()));
			liquid = gas.liquidTank.getFluid(); 
			if (liquid != null && liquid.getFluid() != null)
				reader.setString("liquidTankName", liquid.getFluid().getUnlocalizedName());
			else
				reader.setString("liquidTankName", "");
			reader.setString("liquidTank2", String.format("%s / %s mB", gas.liquidTank2.getFluidAmount(), gas.liquidTank2.getCapacity()));
			liquid = gas.liquidTank2.getFluid(); 
			if (liquid != null && liquid.getFluid() != null)
				reader.setString("liquidTank2Name", liquid.getFluid().getUnlocalizedName());
			else
				reader.setString("liquidTank2Name", "");
			int counter = 0;
			ItemStack stack = gas.inventory.get(1);
			if (!stack.isEmpty() && stack.getItem() instanceof ItemAtmosphericValve)
				counter = stack.getCount();
			reader.setInt("valve", counter);
			return CardState.OK;
		}
		if (te instanceof TileEntityOxygenStorageModule) {
			reader.setInt("type", 8);
			reader.setInt("oxygenStored", ((TileEntityOxygenStorageModule) te).getOxygenStored());
			reader.setInt("oxygenCapacity", ((TileEntityOxygenStorageModule) te).getMaxOxygenStored());
			return CardState.OK;
		}
		if (te instanceof TileEntityEnergyStorageModule) {
			reader.setInt("type", 9);
			reader.setDouble("stored", (double) ((TileEntityEnergyStorageModule) te).storage.getEnergyStoredGC());
			reader.setDouble("capacity", (double) ((TileEntityEnergyStorageModule) te).storage.getCapacityGC());
			return CardState.OK;
		}
		if (te instanceof TileEntitySolar) {
			reader.setInt("type", 10);
			reader.setString("status", GalacticraftHelper.getStatus((TileEntitySolar) te));
			reader.setDouble("stored", (double) ((TileEntitySolar) te).storage.getEnergyStoredGC());
			reader.setDouble("capacity", (double) ((TileEntitySolar) te).storage.getCapacityGC());
			reader.setInt("production", ((TileEntitySolar) te).generateWatts);
			reader.setDouble("boost", Math.round((((TileEntitySolar) te).getSolarBoost() - 1) * 1000) / 10.0D);
			reader.setDouble("sunVisible", Math.round(((TileEntitySolar) te).solarStrength / 9.0F * 1000) / 10.0D);
			return CardState.OK;
		}
		if (te instanceof TileEntityLaunchController ) {
			reader.setInt("type", 11);
			reader.setString("status", GalacticraftHelper.getStatus((TileEntityLaunchController) te));
			reader.setDouble("stored", (double) ((TileEntityLaunchController) te).storage.getEnergyStoredGC());
			reader.setDouble("capacity", (double) ((TileEntityLaunchController) te).storage.getCapacityGC());
			reader.setInt("frequency", ((TileEntityLaunchController) te).frequency);
			reader.setInt("target", ((TileEntityLaunchController) te).destFrequency);
			return CardState.OK;
		}
		if (te instanceof TileEntitySolarArrayController) {
			reader.setInt("type", 12);
			reader.setString("status", GalacticraftHelper.getStatus((TileEntitySolarArrayController) te));
			reader.setDouble("stored", (double) ((TileEntitySolarArrayController) te).storage.getEnergyStoredGC());
			reader.setDouble("capacity", (double) ((TileEntitySolarArrayController) te).storage.getCapacityGC());
			reader.setInt("production", ((TileEntitySolarArrayController) te).generateWatts);
			reader.setDouble("boost", Math.round((((TileEntitySolarArrayController) te).getSolarBoost() - 1) * 1000) / 10.0D);
			//reader.setDouble("sunVisible", Math.round(((TileEntitySolarArrayController) te).solarStrength / 9.0F * 1000) / 10.0D);
			return CardState.OK;
		}*/
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean showLabels) {
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
			result.add(new PanelString("msg.ec.InfoPanelOutputRF", reader.getInt("output"), showLabels));
			break;
		case 5:
			result.add(new PanelString("msg.ec.InfoPanelWater", reader.getString("waterTank"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelOxygen", reader.getString("gasTank1"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelHydrogen", reader.getString("gasTank2"), showLabels));
			break;
		case 6:
			result.add(new PanelString("msg.ec.InfoPanelAtmosphericValve", reader.getInt("valve"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelHydrogen", reader.getString("gasTank1"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelCarbonDioxide", reader.getString("gasTank2"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelMethane", reader.getString("methaneTank"), showLabels));
			result.add(new PanelString("msg.ec.InfoPanelFragmentedCarbon", reader.getInt("items"), showLabels));
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
		List<PanelSetting> result = new ArrayList<PanelSetting>(2);
		result.add(new PanelSetting(I18n.format("msg.ec.cbStatus"), 1, damage));
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelEnergy"), 2, damage));
		return result;
	}

	@Override
	public int getKitFromCard() {
		return ItemCardType.KIT_NUCLEARCRAFT;
	}
}
