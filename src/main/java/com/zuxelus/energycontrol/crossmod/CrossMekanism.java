package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank.Current;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.boiler.BoilerMultiblockData;
import mekanism.common.content.matrix.MatrixMultiblockData;
import mekanism.common.content.network.transmitter.MechanicalPipe;
import mekanism.common.tile.*;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.factory.TileEntityItemStackGasToItemStackFactory;
import mekanism.common.tile.factory.TileEntityMetallurgicInfuserFactory;
import mekanism.common.tile.laser.TileEntityBasicLaser;
import mekanism.common.tile.machine.*;
import mekanism.common.tile.multiblock.TileEntityBoilerCasing;
import mekanism.common.tile.multiblock.TileEntityInductionCasing;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import mekanism.common.tile.transmitter.TileEntityMechanicalPipe;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.EnergyType;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;

public class CrossMekanism extends CrossModBase {

	@Override
	public CompoundNBT getEnergyData(TileEntity te) {
		if (te instanceof TileEntityEnergyCube)
			return setStorage(((TileEntityEnergyCube) te).getEnergyContainer());
		if (te instanceof TileEntityFactory)
			return setStorage(((TileEntityFactory<?>) te).getEnergyContainer());
		if (te instanceof TileEntityElectricMachine)
			return setStorage(((TileEntityElectricMachine) te).getEnergyContainer());
		if (te instanceof TileEntityAdvancedElectricMachine)
			return setStorage(((TileEntityAdvancedElectricMachine) te).getEnergyContainer());
		if (te instanceof TileEntityCombiner)
			return setStorage(((TileEntityCombiner) te).getEnergyContainer());
		if (te instanceof TileEntityAdvancedBoundingBlock) {
			TileEntity tile = te.getLevel().getBlockEntity(((TileEntityAdvancedBoundingBlock)te).getMainPos());
			if (tile instanceof TileEntityDigitalMiner)
				return setStorage(((TileEntityDigitalMiner) tile).getEnergyContainer());
		}
		if (te instanceof TileEntityMetallurgicInfuser)
			return setStorage(((TileEntityMetallurgicInfuser) te).getEnergyContainer());
		if (te instanceof TileEntityTeleporter)
			return setStorage(((TileEntityTeleporter) te).getEnergyContainer());
		if (te instanceof TileEntityElectricPump)
			return setStorage(((TileEntityElectricPump) te).getEnergyContainer());
		if (te instanceof TileEntityChargepad) {
			try {
				Field field = TileEntityChargepad.class.getDeclaredField("energyContainer");
				field.setAccessible(true);
				MachineEnergyContainer<TileEntityChargepad> energyContainer = (MachineEnergyContainer<TileEntityChargepad>) field.get(te);
				return setStorage(energyContainer);
			} catch (Throwable ignored) { }
		}
		if (te instanceof TileEntityRotaryCondensentrator)
			return setStorage(((TileEntityRotaryCondensentrator) te).getEnergyContainer());
		if (te instanceof TileEntityChemicalOxidizer)
			return setStorage(((TileEntityChemicalOxidizer) te).getEnergyContainer());
		if (te instanceof TileEntityChemicalInfuser)
			return setStorage(((TileEntityChemicalInfuser) te).getEnergyContainer());
		if (te instanceof TileEntityElectrolyticSeparator)
			return setStorage(((TileEntityElectrolyticSeparator) te).getEnergyContainer());
		if (te instanceof TileEntityPrecisionSawmill)
			return setStorage(((TileEntityPrecisionSawmill) te).getEnergyContainer());
		if (te instanceof TileEntityChemicalDissolutionChamber)
			return setStorage(((TileEntityChemicalDissolutionChamber) te).getEnergyContainer());
		if (te instanceof TileEntityChemicalWasher)
			return setStorage(((TileEntityChemicalWasher) te).getEnergyContainer());
		if (te instanceof TileEntityChemicalCrystallizer)
			return setStorage(((TileEntityChemicalCrystallizer) te).getEnergyContainer());
		if (te instanceof TileEntitySeismicVibrator)
			return setStorage(((TileEntitySeismicVibrator) te).getEnergyContainer());
		if (te instanceof TileEntityPressurizedReactionChamber)
			return setStorage(((TileEntityPressurizedReactionChamber) te).getEnergyContainer());
		if (te instanceof TileEntityIsotopicCentrifuge)
			return setStorage(((TileEntityIsotopicCentrifuge) te).getEnergyContainer());
		if (te instanceof TileEntityNutritionalLiquifier)
			return setStorage(((TileEntityNutritionalLiquifier) te).getEnergyContainer());
		if (te instanceof TileEntityAntiprotonicNucleosynthesizer)
			return setStorage(((TileEntityAntiprotonicNucleosynthesizer) te).getEnergyContainer());
		if (te instanceof TileEntityResistiveHeater)
			return setStorage(((TileEntityResistiveHeater) te).getEnergyContainer());
		if (te instanceof TileEntityBasicLaser)
			return setStorage(((TileEntityBasicLaser) te).getEnergyContainer());
		if (te instanceof TileEntityInductionCasing) {
			MatrixMultiblockData matrix = ((TileEntityInductionCasing) te).getMultiblock();
			return setStorage(matrix.getEnergyContainer());
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileEntityFluidTank) {
			result.add(toFluidInfo(((TileEntityFluidTank) te).fluidTank));
			return result;
		}
		if (te instanceof TileEntityItemStackGasToItemStackFactory) {
			result.add(toFluidInfo(((TileEntityItemStackGasToItemStackFactory) te).getGasTank()));
			return result;
		}
		if (te instanceof TileEntityMetallurgicInfuserFactory) {
			result.add(toFluidInfo(((TileEntityMetallurgicInfuserFactory) te).getInfusionTank()));
			return result;
		}
		if (te instanceof TileEntityAdvancedElectricMachine) {
			result.add(toFluidInfo(((TileEntityAdvancedElectricMachine) te).gasTank));
			return result;
		}
		if (te instanceof TileEntityMetallurgicInfuser) {
			result.add(toFluidInfo(((TileEntityMetallurgicInfuser) te).infusionTank));
			return result;
		}
		if (te instanceof TileEntityElectricPump) {
			result.add(toFluidInfo(((TileEntityElectricPump) te).fluidTank));
			return result;
		}
		if (te instanceof TileEntityRotaryCondensentrator) {
			result.add(toFluidInfo(((TileEntityRotaryCondensentrator) te).gasTank));
			result.add(toFluidInfo(((TileEntityRotaryCondensentrator) te).fluidTank));
			return result;
		}
		if (te instanceof TileEntityChemicalOxidizer) {
			result.add(toFluidInfo(((TileEntityChemicalOxidizer) te).gasTank));
			return result;
		}
		if (te instanceof TileEntityChemicalInfuser) {
			result.add(toFluidInfo(((TileEntityChemicalInfuser) te).leftTank));
			result.add(toFluidInfo(((TileEntityChemicalInfuser) te).rightTank));
			result.add(toFluidInfo(((TileEntityChemicalInfuser) te).centerTank));
			return result;
		}
		if (te instanceof TileEntityElectrolyticSeparator) {
			result.add(toFluidInfo(((TileEntityElectrolyticSeparator) te).fluidTank));
			result.add(toFluidInfo(((TileEntityElectrolyticSeparator) te).leftTank));
			result.add(toFluidInfo(((TileEntityElectrolyticSeparator) te).rightTank));
			return result;
		}
		if (te instanceof TileEntityChemicalDissolutionChamber) {
			result.add(toFluidInfo(((TileEntityChemicalDissolutionChamber) te).injectTank));
			result.add(toFluidInfo(((TileEntityChemicalDissolutionChamber) te).outputTank.getGasTank()));
			return result;
		}
		if (te instanceof TileEntityChemicalWasher) {
			result.add(toFluidInfo(((TileEntityChemicalWasher) te).fluidTank));
			result.add(toFluidInfo(((TileEntityChemicalWasher) te).inputTank));
			result.add(toFluidInfo(((TileEntityChemicalWasher) te).outputTank));
			return result;
		}
		if (te instanceof TileEntityChemicalCrystallizer) {
			result.add(toFluidInfo(((TileEntityChemicalCrystallizer) te).inputTank));
			return result;
		}
		if (te instanceof TileEntityPressurizedReactionChamber) {
			result.add(toFluidInfo(((TileEntityPressurizedReactionChamber) te).inputFluidTank));
			result.add(toFluidInfo(((TileEntityPressurizedReactionChamber) te).inputGasTank));
			result.add(toFluidInfo(((TileEntityPressurizedReactionChamber) te).outputGasTank));
			return result;
		}
		if (te instanceof TileEntityIsotopicCentrifuge) {
			result.add(toFluidInfo(((TileEntityIsotopicCentrifuge) te).inputTank));
			result.add(toFluidInfo(((TileEntityIsotopicCentrifuge) te).outputTank));
			return result;
		}
		if (te instanceof TileEntityNutritionalLiquifier) {
			result.add(toFluidInfo(((TileEntityNutritionalLiquifier) te).gasTank));
			return result;
		}
		if (te instanceof TileEntityAntiprotonicNucleosynthesizer) {
			result.add(toFluidInfo(((TileEntityAntiprotonicNucleosynthesizer) te).gasTank));
			return result;
		}
		return null;
	}

	public static FluidInfo toFluidInfo(IChemicalTank<?,?> tank) {
		return new FluidInfo(tank.getType().getTranslationKey(), null, tank.getStored(), tank.getCapacity()); // TODO
	}

	public static FluidInfo toFluidInfo(IExtendedFluidTank tank) {
		return new FluidInfo(tank);
	}

	public static FluidInfo toFluidInfo(MergedChemicalTank tank) { // MergedChemicalTank doesn't exist in 1.15
		Current current = tank.getCurrent();
		if (current == Current.EMPTY)
			return new FluidInfo(null, null, 0, 0);
		return toFluidInfo(tank.getTankFromCurrent(current));
	}

	@Override
	public CompoundNBT getCardData(TileEntity te) {
		if (te instanceof TileEntityFactory) {
			CompoundNBT tag = setStorage(((TileEntityFactory<?>) te).getEnergyContainer());
			tag.putDouble("usage", MekanismUtils.convertToDisplay(((TileEntityFactory<?>) te).getLastUsage()).doubleValue());
			tag.putBoolean("active", (((TileEntityFactory<?>) te).getLastUsage()).doubleValue() > 0);
			if (te instanceof TileEntityItemStackGasToItemStackFactory)
				addTank("tank", tag, ((TileEntityItemStackGasToItemStackFactory) te).getGasTank());
			if (te instanceof TileEntityMetallurgicInfuserFactory)
				addTank("tank", tag, ((TileEntityMetallurgicInfuserFactory) te).getInfusionTank());
			return tag;
		}
		if (te instanceof TileEntityElectricMachine) {
			CompoundNBT tag = setStorage(((TileEntityElectricMachine) te).getEnergyContainer());
			addUsage(tag, ((TileEntityElectricMachine) te).getEnergyContainer(), ((TileEntityElectricMachine) te).getActive());
			return tag;
		}
		if (te instanceof TileEntityAdvancedElectricMachine) {
			CompoundNBT tag = setStorage(((TileEntityAdvancedElectricMachine) te).getEnergyContainer());
			addUsage(tag, ((TileEntityAdvancedElectricMachine) te).getEnergyContainer(), ((TileEntityAdvancedElectricMachine) te).getActive());
			addTank("tank", tag, ((TileEntityAdvancedElectricMachine) te).gasTank);
			return tag;
		}
		if (te instanceof TileEntityCombiner) {
			CompoundNBT tag = setStorage(((TileEntityCombiner) te).getEnergyContainer());
			addUsage(tag, ((TileEntityCombiner) te).getEnergyContainer(), ((TileEntityCombiner) te).getActive());
			return tag;
		}
		if (te instanceof TileEntityAdvancedBoundingBlock) {
			TileEntity tile = te.getLevel().getBlockEntity(((TileEntityAdvancedBoundingBlock)te).getMainPos());
			if (tile instanceof TileEntityDigitalMiner) {
				CompoundNBT tag = setStorage(((TileEntityDigitalMiner) tile).getEnergyContainer());
				tag.putBoolean("active", ((TileEntityDigitalMiner) tile).getActive());
				return tag;
			}
		}
		if (te instanceof TileEntityMetallurgicInfuser) {
			CompoundNBT tag = setStorage(((TileEntityMetallurgicInfuser) te).getEnergyContainer());
			addUsage(tag, ((TileEntityMetallurgicInfuser) te).getEnergyContainer(), ((TileEntityMetallurgicInfuser) te).getActive());
			addTank("tank", tag, ((TileEntityMetallurgicInfuser) te).infusionTank);
			return tag;
		}
		if (te instanceof TileEntityTeleporter) {
			CompoundNBT tag = setStorage(((TileEntityTeleporter) te).getEnergyContainer());
			return tag;
		}
		if (te instanceof TileEntityElectricPump) {
			CompoundNBT tag = setStorage(((TileEntityElectricPump) te).getEnergyContainer());
			addTank("tank", tag, ((TileEntityElectricPump) te).fluidTank);
			return tag;
		}
		if (te instanceof TileEntityChargepad) {
			try {
				Field field = TileEntityChargepad.class.getDeclaredField("energyContainer");
				field.setAccessible(true);
				MachineEnergyContainer<TileEntityChargepad> energyContainer = (MachineEnergyContainer<TileEntityChargepad>) field.get(te);
				CompoundNBT tag = setStorage(energyContainer);
				tag.putBoolean("active", ((TileEntityChargepad)te).getActive());
				return tag;
			} catch (Throwable ignored) {
			}
		}
		if (te instanceof TileEntityRotaryCondensentrator) {
			CompoundNBT tag = setStorage(((TileEntityRotaryCondensentrator) te).getEnergyContainer());
			addUsage(tag, ((TileEntityRotaryCondensentrator) te).getEnergyContainer(), ((TileEntityRotaryCondensentrator) te).getActive());
			addTank("tank", tag, ((TileEntityRotaryCondensentrator) te).gasTank);
			addTank("tanks", tag, ((TileEntityRotaryCondensentrator) te).fluidTank);
			return tag;
		}
		if (te instanceof TileEntityChemicalOxidizer) {
			CompoundNBT tag = setStorage(((TileEntityChemicalOxidizer) te).getEnergyContainer());
			addUsage(tag, ((TileEntityChemicalOxidizer) te).getEnergyContainer(), ((TileEntityChemicalOxidizer) te).getActive());
			addTank("tank", tag, ((TileEntityChemicalOxidizer) te).gasTank);
			return tag;
		}
		if (te instanceof TileEntityChemicalInfuser) {
			CompoundNBT tag = setStorage(((TileEntityChemicalInfuser) te).getEnergyContainer());
			addUsage(tag, ((TileEntityChemicalInfuser) te).getEnergyContainer(), ((TileEntityChemicalInfuser) te).getActive());
			addTank("tank", tag, ((TileEntityChemicalInfuser) te).leftTank);
			addTank("tank2", tag, ((TileEntityChemicalInfuser) te).rightTank);
			addTank("tank3", tag, ((TileEntityChemicalInfuser) te).centerTank);
			return tag;
		}
		if (te instanceof TileEntityElectrolyticSeparator) {
			CompoundNBT tag = setStorage(((TileEntityElectrolyticSeparator) te).getEnergyContainer());
			addUsage(tag, ((TileEntityElectrolyticSeparator) te).getEnergyContainer(), ((TileEntityElectrolyticSeparator) te).getActive());
			addTank("tank", tag, ((TileEntityElectrolyticSeparator) te).fluidTank);
			addTank("tank2", tag, ((TileEntityElectrolyticSeparator) te).leftTank);
			addTank("tank3", tag, ((TileEntityElectrolyticSeparator) te).rightTank);
			return tag;
		}
		if (te instanceof TileEntityPrecisionSawmill) {
			CompoundNBT tag = setStorage(((TileEntityPrecisionSawmill) te).getEnergyContainer());
			addUsage(tag, ((TileEntityPrecisionSawmill) te).getEnergyContainer(), ((TileEntityPrecisionSawmill) te).getActive());
			return tag;
		}
		if (te instanceof TileEntityChemicalDissolutionChamber) {
			CompoundNBT tag = setStorage(((TileEntityChemicalDissolutionChamber) te).getEnergyContainer());
			addUsage(tag, ((TileEntityChemicalDissolutionChamber) te).getEnergyContainer(), ((TileEntityChemicalDissolutionChamber) te).getActive());
			addTank("tank", tag, ((TileEntityChemicalDissolutionChamber) te).injectTank);
			addTank("tank2", tag, ((TileEntityChemicalDissolutionChamber) te).outputTank.getGasTank());
			return tag;
		}
		if (te instanceof TileEntityChemicalWasher) {
			CompoundNBT tag = setStorage(((TileEntityChemicalWasher) te).getEnergyContainer());
			addUsage(tag, ((TileEntityChemicalWasher) te).getEnergyContainer(), ((TileEntityChemicalWasher) te).getActive());
			addTank("tank", tag, ((TileEntityChemicalWasher) te).fluidTank);
			addTank("tank2", tag, ((TileEntityChemicalWasher) te).inputTank);
			addTank("tank3", tag, ((TileEntityChemicalWasher) te).outputTank);
			return tag;
		}
		if (te instanceof TileEntityChemicalCrystallizer) {
			CompoundNBT tag = setStorage(((TileEntityChemicalCrystallizer) te).getEnergyContainer());
			addUsage(tag, ((TileEntityChemicalCrystallizer) te).getEnergyContainer(), ((TileEntityChemicalCrystallizer) te).getActive());
			addTank("tank", tag, ((TileEntityChemicalCrystallizer) te).inputTank);
			return tag;
		}
		if (te instanceof TileEntitySeismicVibrator) {
			CompoundNBT tag = setStorage(((TileEntitySeismicVibrator) te).getEnergyContainer());
			return tag;
		}
		if (te instanceof TileEntityPressurizedReactionChamber) {
			CompoundNBT tag = setStorage(((TileEntityPressurizedReactionChamber) te).getEnergyContainer());
			addUsage(tag, ((TileEntityPressurizedReactionChamber) te).getEnergyContainer(), ((TileEntityPressurizedReactionChamber) te).getActive());
			addTank("tank", tag, ((TileEntityPressurizedReactionChamber) te).inputFluidTank);
			addTank("tank2", tag, ((TileEntityPressurizedReactionChamber) te).inputGasTank);
			addTank("tank3", tag, ((TileEntityPressurizedReactionChamber) te).outputGasTank);
			return tag;
		}
		if (te instanceof TileEntityIsotopicCentrifuge) {
			CompoundNBT tag = setStorage(((TileEntityIsotopicCentrifuge) te).getEnergyContainer());
			addUsage(tag, ((TileEntityIsotopicCentrifuge) te).getEnergyContainer(), ((TileEntityIsotopicCentrifuge) te).getActive());
			addTank("tank", tag, ((TileEntityIsotopicCentrifuge) te).inputTank);
			addTank("tank2", tag, ((TileEntityIsotopicCentrifuge) te).outputTank);
			return tag;
		}
		if (te instanceof TileEntityNutritionalLiquifier) {
			CompoundNBT tag = setStorage(((TileEntityNutritionalLiquifier) te).getEnergyContainer());
			addUsage(tag, ((TileEntityNutritionalLiquifier) te).getEnergyContainer(), ((TileEntityNutritionalLiquifier) te).getActive());
			addTank("tank", tag, ((TileEntityNutritionalLiquifier) te).gasTank);
			return tag;
		}
		if (te instanceof TileEntityAntiprotonicNucleosynthesizer) {
			CompoundNBT tag = setStorage(((TileEntityAntiprotonicNucleosynthesizer) te).getEnergyContainer());
			addUsage(tag, ((TileEntityAntiprotonicNucleosynthesizer) te).getEnergyContainer(), ((TileEntityAntiprotonicNucleosynthesizer) te).getActive());
			tag.putDouble("usage", MekanismUtils.convertToDisplay(((TileEntityAntiprotonicNucleosynthesizer) te).getEnergyUsed()).doubleValue());
			addTank("tank", tag, ((TileEntityAntiprotonicNucleosynthesizer) te).gasTank);
			return tag;
		}
		if (te instanceof TileEntityResistiveHeater) {
			CompoundNBT tag = setStorage(((TileEntityResistiveHeater) te).getEnergyContainer());
			addUsage(tag, ((TileEntityResistiveHeater) te).getEnergyContainer(), ((TileEntityResistiveHeater) te).getActive());
			tag.putString("temp", getTempString(((TileEntityResistiveHeater) te).getTotalTemperature()));
			return tag;
		}
		if (te instanceof TileEntityBasicLaser) {
			CompoundNBT tag = setStorage(((TileEntityBasicLaser) te).getEnergyContainer());
			return tag;
		}
		if (te instanceof TileEntityBoilerCasing) {
			return boilerTag(((TileEntityBoilerCasing) te).getMultiblock());
		}
		if (te instanceof TileEntityInductionCasing) {
			MatrixMultiblockData matrix = ((TileEntityInductionCasing) te).getMultiblock();
			CompoundNBT tag = setStorage(matrix.getEnergyContainer());
			tag.putDouble("input", MekanismUtils.convertToDisplay(matrix.getLastInput()).doubleValue());
			tag.putDouble("output", MekanismUtils.convertToDisplay(matrix.getLastOutput()).doubleValue());
			return tag;
		}
		return null;
	}

	private static void addUsage(CompoundNBT tag, MachineEnergyContainer<?> energyContainer, Boolean active) {
		if (active) {
			tag.putDouble("usage", MekanismUtils.convertToDisplay(energyContainer.getEnergyPerTick()).doubleValue());
			tag.putBoolean("active", true);
		} else {
			tag.putDouble("usage", 0);
			tag.putBoolean("active", false);
		}
	}

	public static void addTank(String name, CompoundNBT tag, IChemicalTank<?,?> tank) {
		if (tank.isEmpty())
			tag.putString(name, "N/A");
		else
			tag.putString(name, String.format("%s: %s mB", LanguageMap.getInstance().getOrDefault(tank.getType().getTranslationKey()), tank.getStored()));
	}

	public static void addTank(String name, CompoundNBT tag, IExtendedFluidTank tank) {
		if (tank.isEmpty())
			tag.putString(name, "N/A");
		else
			tag.putString(name, String.format("%s: %s mB", LanguageMap.getInstance().getOrDefault(tank.getFluid().getTranslationKey()), tank.getFluidAmount()));
	}

	public static void addTank(String name, CompoundNBT tag, MergedChemicalTank tank) { // MergedChemicalTank doesn't exist in 1.15
		Current current = tank.getCurrent();
		if (current == Current.EMPTY)
			tag.putString(name, "N/A");
		else
			addTank(name, tag, tank.getTankFromCurrent(current));
	}

	public static CompoundNBT boilerTag(BoilerMultiblockData block) {
		if (block == null)
			return null;
		CompoundNBT tag = new CompoundNBT();
		tag.putDouble("boil_rate", block.lastBoilRate);
		tag.putString("temp", getTempString(block.heatCapacitor.getTemperature()));
		addTank("tank", tag, ((BoilerMultiblockData) block).cooledCoolantTank);
		addTank("tank2", tag, ((BoilerMultiblockData) block).waterTank);
		addTank("tank4", tag, ((BoilerMultiblockData) block).steamTank);
		addTank("tank3", tag, ((BoilerMultiblockData) block).superheatedCoolantTank);
		return tag;
	}

	public static CompoundNBT setStorage(IEnergyContainer container) {
		CompoundNBT tag = new CompoundNBT();
		EnergyType euType = MekanismConfig.general.energyUnit.get();
		tag.putString("euType", euType.name());
		tag.putDouble("storage", MekanismUtils.convertToDisplay(container.getEnergy()).doubleValue());
		tag.putDouble("maxStorage", MekanismUtils.convertToDisplay(container.getMaxEnergy()).doubleValue());
		return tag;
	}

	public static String getTempString(double temp) {
		switch (MekanismConfig.general.tempUnit.get()) {
		case K:
			return formatTemp(TemperatureUnit.KELVIN, temp);
		case C:
			return formatTemp(TemperatureUnit.CELSIUS, temp);
		case R:
			return formatTemp(TemperatureUnit.RANKINE, temp);
		case F:
			return formatTemp(TemperatureUnit.FAHRENHEIT, temp);
		case STP:
			return formatTemp(TemperatureUnit.AMBIENT, temp);
		default:
			return "";
		}
	}

	private static String formatTemp(TemperatureUnit unit, double temp) {
		return String.format("%.3f %s", unit.convertFromK(temp, true), unit.getSymbol());
	}

	@Override
	public IEnergyStorage getEnergyStorage(TileEntity te) {
		return null;
	}

	@Override
	public IFluidTank getPipeTank(TileEntity te) {
		if (te instanceof TileEntityMechanicalPipe) {
			MechanicalPipe pipe = ((TileEntityMechanicalPipe) te).getTransmitter();
			if (pipe != null && pipe.getTransmitterNetwork() != null)
				return pipe.getTransmitterNetwork().fluidTank;
		}
		if (te instanceof TileEntityFluidTank)
			return ((TileEntityFluidTank) te).fluidTank;
		return null;
	}
}
