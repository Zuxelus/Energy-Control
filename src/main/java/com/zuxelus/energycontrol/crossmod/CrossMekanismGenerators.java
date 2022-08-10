package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import mekanism.api.math.FloatingLong;
import mekanism.api.text.EnumColor;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.text.BooleanStateDisplay;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.tile.*;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorBlock;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CrossMekanismGenerators extends CrossModBase {

	@Override
	public CompoundTag getEnergyData(BlockEntity te) {
		if (te instanceof TileEntityGenerator)
			return CrossMekanism.setStorage(((TileEntityGenerator) te).getEnergyContainer());
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(BlockEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileEntityBioGenerator) {
			result.add(new FluidInfo(((TileEntityBioGenerator) te).bioFuelTank));
			return result;
		}
		if (te instanceof TileEntityFissionReactorCasing) {
			FissionReactorMultiblockData block = ((TileEntityFissionReactorCasing) te).getMultiblock();
			if (block == null)
				return null;
			result.add(CrossMekanism.toFluidInfo(block.fluidCoolantTank));
			result.add(CrossMekanism.toFluidInfo(block.fuelTank));
			result.add(CrossMekanism.toFluidInfo(block.heatedCoolantTank));
			result.add(CrossMekanism.toFluidInfo(block.wasteTank));
		}
		if (te instanceof TileEntityTurbineCasing) {
			TurbineMultiblockData block = ((TileEntityTurbineCasing) te).getMultiblock();
			if (block == null)
				return null;
			result.add(CrossMekanism.toFluidInfo(block.gasTank));
			result.add(CrossMekanism.toFluidInfo(block.ventTank));
			return result;
		}
		if (te instanceof TileEntityFusionReactorBlock) {
			FusionReactorMultiblockData block = ((TileEntityFusionReactorBlock) te).getMultiblock();
			if (block == null)
				return null;
			result.add(CrossMekanism.toFluidInfo(block.deuteriumTank));
			result.add(CrossMekanism.toFluidInfo(block.tritiumTank));
			result.add(CrossMekanism.toFluidInfo(block.fuelTank));
			result.add(CrossMekanism.toFluidInfo(block.waterTank));
			result.add(CrossMekanism.toFluidInfo(block.steamTank));
		}
		return null;
	}

	@Override
	public CompoundTag getCardData(BlockEntity te) {
		if (te instanceof TileEntityGenerator) {
			CompoundTag tag = CrossMekanism.setStorage(((TileEntityGenerator) te).getEnergyContainer());
			if (te instanceof TileEntityHeatGenerator) {
				double production = ((TileEntityHeatGenerator) te).getProducingEnergy().doubleValue();
				tag.putDouble("production", production);
				tag.putBoolean("active", production > 0);
				tag.putString("temp", CrossMekanism.getTempString(((TileEntityGenerator) te).getTotalTemperature()));
			}
			if (te instanceof TileEntityWindGenerator) {
				double production = MekanismUtils.convertToDisplay(MekanismGeneratorsConfig.generators.windGenerationMin.get().multiply(((TileEntityWindGenerator)te).getCurrentMultiplier())).doubleValue();
				tag.putDouble("production", production);
				tag.putBoolean("active", production > 0);
			}
			if (te instanceof TileEntitySolarGenerator) {
				double production = MekanismUtils.convertToDisplay(((TileEntitySolarGenerator)te).getLastProductionAmount()).doubleValue();
				tag.putDouble("production", production);
				tag.putBoolean("active", production > 0);
			}
			if (te instanceof TileEntityBioGenerator) {
				if (((TileEntityBioGenerator)te).getActive()) {
					tag.putDouble("production", MekanismUtils.convertToDisplay(MekanismGeneratorsConfig.generators.bioGeneration.get()).doubleValue());
					tag.putBoolean("active", true);
				} else {
					tag.putDouble("production", 0);
					tag.putBoolean("active", false);
				}
				CrossMekanism.addTank("tank", tag, ((TileEntityBioGenerator) te).bioFuelTank);
			}
			return tag;
		}
		if (te instanceof TileEntityFissionReactorCasing) {
			return fissionReactorTag(((TileEntityFissionReactorCasing) te).getMultiblock());
		}
		if (te instanceof TileEntityTurbineCasing) {
			return turbineTag(((TileEntityTurbineCasing) te).getMultiblock());
		}
		if (te instanceof TileEntityFusionReactorBlock) {
			FusionReactorMultiblockData block = ((TileEntityFusionReactorBlock) te).getMultiblock();
			if (block == null)
				return null;
			CompoundTag tag = CrossMekanism.setStorage(block.energyContainer);
			tag.putDouble("production", MekanismUtils.convertToDisplay(block.getPassiveGeneration(false, false)).doubleValue());
			tag.putString("temp", CrossMekanism.getTempString(block.heatCapacitor.getTemperature()));
			tag.putString("plasma", CrossMekanism.getTempString(block.getLastPlasmaTemp()));
			tag.putDouble("injection_rate", block.getInjectionRate());
			CrossMekanism.addTank("tank", tag, block.deuteriumTank);
			CrossMekanism.addTank("tank2", tag, block.tritiumTank);
			CrossMekanism.addTank("tank3", tag, block.fuelTank);
			CrossMekanism.addTank("tank4", tag, block.waterTank);
			CrossMekanism.addTank("tank5", tag, block.steamTank);
			return tag;
		}
		return null;
	}

	public static CompoundTag fissionReactorTag(FissionReactorMultiblockData block) {
		if (block == null)
			return null;
		CompoundTag tag = new CompoundTag();
		if (block.isActive())
			tag.putString("status", EnumColor.BRIGHT_GREEN.code + BooleanStateDisplay.ActiveDisabled.of(block.isActive()).getTextComponent().getString());
		else
			tag.putString("status", EnumColor.RED.code + BooleanStateDisplay.ActiveDisabled.of(block.isActive()).getTextComponent().getString());
		tag.putDouble("burn_rate", block.lastBurnRate);
		tag.putDouble("heat_rate", block.lastBoilRate);
		tag.putDouble("rate_limit", block.rateLimit);
		tag.putString("temp", CrossMekanism.getTempString(block.heatCapacitor.getTemperature()));
		CrossMekanism.addTank("tank", tag, block.fluidCoolantTank);
		CrossMekanism.addTank("tank2", tag, block.fuelTank);
		CrossMekanism.addTank("tank3", tag, block.heatedCoolantTank);
		CrossMekanism.addTank("tank4", tag, block.wasteTank);
		return tag;
	}

	public static CompoundTag turbineTag(TurbineMultiblockData block) {
		if (block == null)
			return null;
		CompoundTag tag = CrossMekanism.setStorage(block.energyContainer);
		FloatingLong energyMultiplier = ((FloatingLong) MekanismConfig.general.maxEnergyPerSteam.get()).divide(28L).multiply(Math.min(block.blades, block.coils * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.get()));
		tag.putDouble("production", MekanismUtils.convertToDisplay(energyMultiplier.multiply(block.clientFlow)).doubleValue());
		tag.putDouble("flow_rate", block.clientFlow);
		tag.putBoolean("active", block.clientRotation > 0);
		CrossMekanism.addTank("tank", tag, block.gasTank);
		CrossMekanism.addTank("tank2", tag,block.ventTank);
		return tag;
	}
}
