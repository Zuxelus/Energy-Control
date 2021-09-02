package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import mekanism.api.math.FloatingLong;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.turbine.SynchronizedTurbineData;
import mekanism.generators.common.tile.*;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class CrossMekanismGenerators extends CrossModBase {

	@Override
	public CompoundNBT getEnergyData(TileEntity te) {
		if (te instanceof TileEntityGenerator)
			return CrossMekanism.setStorage(((TileEntityGenerator) te).getEnergyContainer());
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileEntityBioGenerator) {
			result.add(new FluidInfo(((TileEntityBioGenerator) te).bioFuelTank));
			return result;
		}
		if (te instanceof TileEntityTurbineCasing) {
			SynchronizedTurbineData block = ((TileEntityTurbineCasing) te).structure;
			if (block == null)
				return null;
			result.add(CrossMekanism.toFluidInfo(block.gasTank));
			result.add(CrossMekanism.toFluidInfo(block.ventTank));
			return result;
		}
		return null;
	}

	@Override
	public CompoundNBT getCardData(TileEntity te) {
		if (te instanceof TileEntityGenerator) {
			CompoundNBT tag = CrossMekanism.setStorage(((TileEntityGenerator) te).getEnergyContainer());
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
		if (te instanceof TileEntityTurbineCasing) {
			return turbineTag(((TileEntityTurbineCasing) te).structure);
		}
		return null;
	}

	public static CompoundNBT turbineTag(SynchronizedTurbineData block) {
		if (block == null)
			return null;
		CompoundNBT tag = CrossMekanism.setStorage(block.energyContainer);
		FloatingLong energyMultiplier = ((FloatingLong) MekanismConfig.general.maxEnergyPerSteam.get()).divide(28L).multiply(Math.min(block.blades, block.coils * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.get()));
		tag.putDouble("production", MekanismUtils.convertToDisplay(energyMultiplier.multiply(block.clientFlow)).doubleValue());
		tag.putDouble("flow_rate", block.clientFlow);
		tag.putBoolean("active", block.clientRotation > 0);
		CrossMekanism.addTank("tank", tag, block.gasTank);
		CrossMekanism.addTank("tank2", tag,block.ventTank);
		return tag;
	}
}
