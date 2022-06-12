package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.items.cards.ItemCardEnderIO;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitEnderIO;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.machine.baselegacy.AbstractGeneratorEntity;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredMachineEntity;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.machines.machine.generator.combustion.CombustionMath;
import crazypants.enderio.machines.machine.generator.combustion.TileCombustionGenerator;
import crazypants.enderio.machines.machine.generator.stirling.TileStirlingGenerator;
import crazypants.enderio.machines.machine.solar.TileSolarPanel;
import crazypants.enderio.machines.machine.vat.TileVat;
import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class CrossEnderIO extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(DataHelper.EUTYPE, "µI");
		if (te instanceof AbstractPoweredMachineEntity) {
			tag.setDouble(DataHelper.ENERGY, ((AbstractPoweredMachineEntity) te).getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((AbstractPoweredMachineEntity) te).getMaxEnergyStored());
			return tag;
		}
		if (te instanceof TileCapBank) {
			tag.setDouble(DataHelper.ENERGY, ((TileCapBank) te).getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileCapBank) te).getMaxEnergyStored());
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
	public NBTTagCompound getCardData(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof AbstractPoweredMachineEntity) {
			AbstractPoweredMachineEntity machine = (AbstractPoweredMachineEntity) te;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.ENERGY, machine.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, machine.getMaxEnergyStored());
			tag.setBoolean(DataHelper.ACTIVE, machine.isActive());
			if (machine.getPowerLossPerTick() > 0)
				tag.setDouble("leakage", machine.getPowerLossPerTick());
			if (te instanceof AbstractGeneratorEntity) {
				if (te instanceof TileStirlingGenerator) {
					if (machine.isActive())
						tag.setDouble(DataHelper.OUTPUT, machine.getPowerUsePerTick());
					else
						tag.setDouble(DataHelper.OUTPUT, 0);
					tag.setDouble(DataHelper.EFFICIENCY, ((TileStirlingGenerator) te).getBurnEfficiency() * 100);
				}
				if  (te instanceof TileCombustionGenerator) {
					TileCombustionGenerator comb = (TileCombustionGenerator) te;
					tag.setDouble(DataHelper.OUTPUT, comb.getGeneratedLastTick());
					FluidInfo.addTank(DataHelper.TANK, tag, comb.getFuelTank());
					FluidInfo.addTank(DataHelper.TANK2, tag, comb.getCoolantTank());
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
					FluidInfo.addTank(DataHelper.TANK, tag, ((TileVat) te).getInputTank(new FluidStack(FluidRegistry.WATER,1000)));
					FluidInfo.addTank(DataHelper.TANK2, tag, ((TileVat) te).getOutputTanks()[0]);
				}
			}
			return tag;
		}
		if (te instanceof TileCapBank) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.ENERGY, ((TileCapBank) te).getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileCapBank) te).getMaxEnergyStored());
			tag.setDouble("maxInput", ((TileCapBank) te).getMaxInput());
			tag.setDouble("maxOutput", ((TileCapBank) te).getMaxOutput());
			tag.setDouble(DataHelper.DIFF, ((TileCapBank) te).getAverageIOPerTick());
			return tag;
		}
		if (te instanceof TileSolarPanel) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileSolarPanel) te).getEnergyStored() > 0);
			tag.setDouble(DataHelper.OUTPUT, ((TileSolarPanel) te).getEnergyStored());
			tag.setDouble("maxOutput", ((TileSolarPanel) te).getMaxEnergyStored());
			return tag;
		}
		return null;
	}

	@Override
	public void registerItems(Register<Item> event) {
		ItemKitMain.register(ItemKitEnderIO::new);
		ItemCardMain.register(ItemCardEnderIO::new);
	}
}
