package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import mekanism.common.config.MekanismConfig;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.FusionReactor;
import mekanism.generators.common.content.turbine.SynchronizedTurbineData;
import mekanism.generators.common.tile.*;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;
import mekanism.generators.common.tile.reactor.TileEntityReactorFrame;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class CrossMekanismGenerators extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		String euType = CrossMekanism.getEUType();
		if (te instanceof TileEntityTurbineCasing) {
			SynchronizedTurbineData turbine = ((TileEntityTurbineCasing) te).structure;
			if (turbine == null)
				return null;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", euType);
			tag.setDouble("storage", MekanismUtils.convertToDisplay(turbine.electricityStored));
			tag.setDouble("maxStorage", MekanismUtils.convertToDisplay(turbine.getEnergyCapacity()));
			return tag;
		}
		if (te instanceof TileEntityReactorFrame) {
			FusionReactor reactor = ((TileEntityReactorFrame) te).fusionReactor;
			if (reactor == null)
				return null;
			TileEntityReactorController controller = reactor.controller;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", euType);
			tag.setDouble("storage", MekanismUtils.convertToDisplay(controller.electricityStored));
			tag.setDouble("maxStorage", MekanismUtils.convertToDisplay(controller.maxEnergy));
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileEntityHeatGenerator) {
			result.add(new FluidInfo(((TileEntityHeatGenerator) te).lavaTank));
			return result;
		}
		if (te instanceof TileEntityGasGenerator) {
			result.add(CrossMekanism.toFluidInfo(((TileEntityGasGenerator) te).fuelTank));
			return result;
		}
		if (te instanceof TileEntityTurbineCasing) {
			SynchronizedTurbineData turbine = ((TileEntityTurbineCasing) te).structure;
			if (turbine == null)
				return null;
			NBTTagCompound tag = new NBTTagCompound();
			result.add(new FluidInfo(turbine.fluidStored,turbine.getFluidCapacity()));
			return result;
		}
		if (te instanceof TileEntityReactorFrame) {
			FusionReactor reactor = ((TileEntityReactorFrame) te).fusionReactor;
			if (reactor == null)
				return null;
			TileEntityReactorController controller = reactor.controller;
			result.add(CrossMekanism.toFluidInfo(controller.deuteriumTank));
			result.add(CrossMekanism.toFluidInfo(controller.tritiumTank));
			result.add(CrossMekanism.toFluidInfo(controller.fuelTank));
			result.add(new FluidInfo(controller.waterTank));
			result.add(new FluidInfo(controller.steamTank));
			return result;
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		if (te instanceof TileEntityGenerator) {
			NBTTagCompound tag = CrossMekanism.setStorage(te);
			if (te instanceof TileEntityHeatGenerator) {
				double production = MekanismUtils.convertToDisplay(((TileEntityHeatGenerator) te).producingEnergy);
				tag.setDouble("production", production);
				tag.setBoolean("active", production > 0);
				tag.setString("temp", CrossMekanism.getTempString(((TileEntityHeatGenerator) te).temperature));
				CrossMekanism.addTank("tank", tag, ((TileEntityHeatGenerator) te).lavaTank);
			}
			if (te instanceof TileEntityWindGenerator) {
				double production = MekanismUtils.convertToDisplay((MekanismConfig.current()).generators.windGenerationMin.val() * ((TileEntityWindGenerator) te).getMultiplier());
				tag.setDouble("production", production);
				tag.setBoolean("active", production > 0);
			}
			if (te instanceof TileEntitySolarGenerator) {
				double production = MekanismUtils.convertToDisplay(((TileEntitySolarGenerator)te).getProduction());
				tag.setDouble("production", production);
				tag.setBoolean("active", production > 0);
			}
			if (te instanceof TileEntityGasGenerator) {
				if (((TileEntityGasGenerator) te).getActive()) {
					tag.setDouble("production", MekanismUtils.convertToDisplay(((TileEntityGasGenerator) te).generationRate * ((TileEntityGasGenerator) te).clientUsed));
					tag.setBoolean("active", true);
				} else {
					tag.setDouble("production", 0);
					tag.setBoolean("active", false);
				}
				CrossMekanism.addTank("tank", tag, ((TileEntityGasGenerator) te).fuelTank);
			}
			if (te instanceof TileEntityBioGenerator) {
				if (((TileEntityBioGenerator) te).getActive()) {
					tag.setDouble("production", MekanismUtils.convertToDisplay(((MekanismConfig.current()).generators.bioGeneration.val())));
					tag.setBoolean("active", true);
				} else {
					tag.setDouble("production", 0);
					tag.setBoolean("active", false);
				}
				CrossMekanism.addTank("tank", tag, ((TileEntityBioGenerator) te).bioFuelSlot, "bioethanol");
			}
			return tag;
		}
		if (te instanceof TileEntityTurbineCasing) {
			SynchronizedTurbineData turbine = ((TileEntityTurbineCasing) te).structure;
			if (turbine == null)
				return null;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", CrossMekanism.getEUType());
			tag.setDouble("storage", MekanismUtils.convertToDisplay(turbine.electricityStored));
			tag.setDouble("maxStorage", MekanismUtils.convertToDisplay(turbine.getEnergyCapacity()));
			
			double energyMultiplier = (MekanismConfig.current()).general.maxEnergyPerSteam.val() / 28.0D * Math.min(turbine.blades, turbine.coils * (MekanismConfig.current()).generators.turbineBladesPerCoil.val());
			double rate = turbine.lowerVolume * turbine.getDispersers() * (MekanismConfig.current()).generators.turbineDisperserGasFlow.val();
			tag.setDouble("production", MekanismUtils.convertToDisplay(turbine.clientFlow * energyMultiplier));
			tag.setDouble("flow_rate", turbine.clientFlow);
			tag.setDouble("max_flow_rate", Math.min(rate, turbine.vents * (MekanismConfig.current()).generators.turbineVentGasFlow.val()));
			tag.setBoolean("active", turbine.clientRotation > 0);
			CrossMekanism.addTank("tank", tag, turbine.fluidStored);
			return tag;
		}
		if (te instanceof TileEntityReactorFrame) {
			FusionReactor reactor = ((TileEntityReactorFrame) te).fusionReactor;
			if (reactor == null)
				return null;
			TileEntityReactorController controller = reactor.controller;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", CrossMekanism.getEUType());
			tag.setDouble("storage", MekanismUtils.convertToDisplay(controller.electricityStored));
			tag.setDouble("maxStorage", MekanismUtils.convertToDisplay(controller.maxEnergy));
			tag.setDouble("production", MekanismUtils.convertToDisplay(reactor.getPassiveGeneration(false, true)));
			tag.setString("temp", CrossMekanism.getTempString(reactor.lastCaseTemperature));
			tag.setString("plasma", CrossMekanism.getTempString(reactor.plasmaTemperature));
			tag.setDouble("injection_rate", reactor.injectionRate);
			CrossMekanism.addTank("tank", tag, controller.deuteriumTank);
			CrossMekanism.addTank("tank2", tag, controller.tritiumTank);
			CrossMekanism.addTank("tank3", tag, controller.fuelTank);
			CrossMekanism.addTank("tank4", tag, controller.waterTank);
			CrossMekanism.addTank("tank5", tag, controller.steamTank);
			return tag;
		}
		return null;
	}
}
