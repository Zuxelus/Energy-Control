package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.items.cards.ItemCardExtremeReactors;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitExtremeReactors;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort.Direction;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPartBase;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartBase;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class CrossExtremeReactors extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(DataHelper.EUTYPE, "FE");
		if (te instanceof TileEntityReactorPartBase) {
			MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
			if (reactor == null)
				return null;

			tag.setDouble(DataHelper.ENERGY, reactor.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, reactor.getEnergyCapacity());
			return tag;
		}
		if (te instanceof TileEntityTurbinePartBase) {
			MultiblockTurbine turbine = ((TileEntityTurbinePartBase) te).getTurbine();
			if (turbine == null)
				return null;

			tag.setDouble(DataHelper.ENERGY, turbine.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, turbine.getEnergyCapacity());
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		if (te instanceof TileEntityReactorPartBase) {
			MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
			if (reactor == null)
				return null;
			if (reactor.isPassivelyCooled())
				return null;
			FluidTankInfo[] info = reactor.getCoolantContainer().getTankInfo(-1);
			List<FluidInfo> result = new ArrayList<>();
			for (FluidTankInfo tank : info)
				result.add(new FluidInfo(tank.fluid, tank.capacity));
			return result;
		}
		if (te instanceof TileEntityTurbinePartBase) {
			MultiblockTurbine turbine = ((TileEntityTurbinePartBase) te).getTurbine();
			if (turbine == null)
				return null;

			IFluidHandler input = turbine.getFluidHandler(Direction.Input);
			IFluidHandler output = turbine.getFluidHandler(Direction.Output);
			List<FluidInfo> result = new ArrayList<>();
			result.add(new FluidInfo(input.getTankProperties()[0].getContents(), input.getTankProperties()[0].getCapacity()));
			result.add(new FluidInfo(output.getTankProperties()[0].getContents(), output.getTankProperties()[0].getCapacity()));
			return result;
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		if (te instanceof TileEntityReactorPartBase) {
			MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
			if (reactor == null)
				return null;

			tag.setBoolean(DataHelper.ACTIVE, reactor.getActive());
			tag.setBoolean("cooling", reactor.isPassivelyCooled());
			tag.setDouble("fuelHeat", reactor.getFuelHeat());
			tag.setDouble(DataHelper.HEAT, reactor.getReactorHeat());
			tag.setDouble(DataHelper.ENERGY, reactor.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, reactor.getEnergyCapacity());
			tag.setDouble(DataHelper.OUTPUT, reactor.getEnergyGeneratedLastTick());
			tag.setInteger("rods", reactor.getFuelRodCount());
			tag.setInteger("fuel", reactor.getFuelAmount());
			tag.setInteger("waste", reactor.getWasteAmount());
			tag.setInteger("fuelCapacity", reactor.getCapacity());
			tag.setDouble("consumption", reactor.getFuelConsumedLastTick());
			BlockPos min = reactor.getMinimumCoord();
			BlockPos max = reactor.getMaximumCoord();
			tag.setString("size", String.format("%sx%sx%s",max.getX() - min.getX() + 1, max.getY() - min.getY() + 1, max.getZ() - min.getZ() + 1));
			return tag;
		}
		if (te instanceof TileEntityTurbinePartBase) {
			MultiblockTurbine turbine = ((TileEntityTurbinePartBase) te).getTurbine();
			if (turbine == null)
				return null;

			tag.setBoolean(DataHelper.ACTIVE, turbine.getActive());
			tag.setDouble(DataHelper.ENERGY, turbine.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, turbine.getEnergyCapacity());
			tag.setDouble(DataHelper.OUTPUT, turbine.getEnergyGeneratedLastTick());
			tag.setDouble("speed", turbine.getRotorSpeed());
			tag.setDouble("speedMax", turbine.getMaxRotorSpeed());
			tag.setDouble("efficiency", turbine.getRotorEfficiencyLastTick());
			tag.setDouble("consumption", turbine.getFluidConsumedLastTick());
			tag.setInteger("blades", turbine.getNumRotorBlades());
			tag.setInteger("mass", turbine.getRotorMass());
			BlockPos min = turbine.getMinimumCoord();
			BlockPos max = turbine.getMaximumCoord();
			tag.setString("size", String.format("%sx%sx%s",max.getX() - min.getX() + 1, max.getY() - min.getY() + 1, max.getZ() - min.getZ() + 1));
			return tag;
		}
		return null;
	}

	@Override
	public int getHeat(World world, BlockPos pos) {
		TileEntity te;
		for (EnumFacing dir : EnumFacing.VALUES) {
			te = world.getTileEntity(pos.offset(dir));
			if (te instanceof TileEntityReactorPartBase) {
				MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
				if (reactor != null)
					return (int) reactor.getReactorHeat();
			}
		}
		return -1;
	}

	@Override
	public void registerItems(Register<Item> event) {
		ItemKitMain.register(ItemKitExtremeReactors::new);
		ItemCardMain.register(ItemCardExtremeReactors::new);
	}
}
