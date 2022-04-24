package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.machine.baselegacy.AbstractGeneratorEntity;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredMachineEntity;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.machines.machine.alloy.TileAlloySmelter;
import crazypants.enderio.machines.machine.generator.combustion.CombustionMath;
import crazypants.enderio.machines.machine.generator.combustion.TileCombustionGenerator;
import crazypants.enderio.machines.machine.generator.stirling.TileStirlingGenerator;
import crazypants.enderio.machines.machine.solar.TileSolarPanel;
import crazypants.enderio.machines.machine.vat.TileVat;
import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWindKineticGenerator;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.tile.TileEntityFluidicPlenisher;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class CrossEnderIO extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof AbstractPoweredMachineEntity) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", "µI");
			tag.setDouble("storage", ((AbstractPoweredMachineEntity) te).getEnergyStored());
			tag.setDouble("maxStorage", ((AbstractPoweredMachineEntity) te).getMaxEnergyStored());
			return tag;
		}
		if (te instanceof TileCapBank) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", "µI");
			tag.setDouble("storage", ((TileCapBank) te).getEnergyStored());
			tag.setDouble("maxStorage", ((TileCapBank) te).getMaxEnergyStored());
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		if (te instanceof AbstractPoweredMachineEntity) {
			AbstractPoweredMachineEntity machine = (AbstractPoweredMachineEntity) te;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", "µI");
			tag.setDouble("storage", machine.getEnergyStored());
			tag.setDouble("maxStorage", machine.getMaxEnergyStored());
			tag.setBoolean("active", machine.isActive());
			if (machine.getPowerLossPerTick() > 0)
				tag.setDouble("leakage", machine.getPowerLossPerTick());
			if (te instanceof AbstractGeneratorEntity) {
				if (te instanceof TileStirlingGenerator) {
					if (machine.isActive())
						tag.setDouble("output", machine.getPowerUsePerTick());
					else
						tag.setDouble("output", 0);
					tag.setDouble("efficiency", ((TileStirlingGenerator) te).getBurnEfficiency() * 100);
				}
				if  (te instanceof TileCombustionGenerator) {
					TileCombustionGenerator comb = (TileCombustionGenerator) te;
					tag.setDouble("output", comb.getGeneratedLastTick());
					FluidInfo.addTank("tank", tag, comb.getFuelTank());
					FluidInfo.addTank("tank2", tag, comb.getCoolantTank());
					try {
						Field field = AbstractPoweredMachineEntity.class.getDeclaredField("maxEnergyUsed");
						field.setAccessible(true);
						ICapacitorKey maxEnergyUsed = (ICapacitorKey) field.get(te);
						CombustionMath math = new CombustionMath(
							CombustionMath.toCoolant(comb.getCoolantTank()),
							CombustionMath.toFuel(comb.getFuelTank()),
							maxEnergyUsed.getFloat(comb.getCapacitorData()), CapacitorKey.LEGACY_ENERGY_EFFICIENCY.getFloat(comb.getCapacitorData()));
						tag.setDouble("usage1", math.getTicksPerFuel());
						tag.setDouble("usage2", math.getTicksPerCoolant());
					} catch (Throwable t) { }
				}
			}
			if (te instanceof AbstractPoweredTaskEntity) {
				if (machine.isActive())
					tag.setDouble("usage", machine.getPowerUsePerTick());
				else
					tag.setDouble("usage", 0);
				if (te instanceof TileVat) {
					FluidInfo.addTank("tank", tag, ((TileVat) te).getInputTank(new FluidStack(FluidRegistry.WATER,1000)));
					FluidInfo.addTank("tank2", tag, ((TileVat) te).getOutputTanks()[0]);
				}
			}
			return tag;
		}
		if (te instanceof TileCapBank) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", "µI");
			tag.setDouble("storage", ((TileCapBank) te).getEnergyStored());
			tag.setDouble("maxStorage", ((TileCapBank) te).getMaxEnergyStored());
			tag.setDouble("maxInput", ((TileCapBank) te).getMaxInput());
			tag.setDouble("maxOutput", ((TileCapBank) te).getMaxOutput());
			tag.setDouble("difference", ((TileCapBank) te).getAverageIOPerTick());
			return tag;
		}
		if (te instanceof TileSolarPanel) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", "µI");
			tag.setBoolean("active", ((TileSolarPanel) te).getEnergyStored() > 0);
			tag.setDouble("output", ((TileSolarPanel) te).getEnergyStored());
			tag.setDouble("maxOutput", ((TileSolarPanel) te).getMaxEnergyStored());
			return tag;
		}
		return null;
	}
}
