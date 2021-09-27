package com.zuxelus.energycontrol.crossmod;

import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort.Direction;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPartBase;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.utils.FluidInfo;

public class CrossBigReactors extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof TileEntityReactorPartBase) {
			MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
			if (reactor == null)
				return null;

			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", "FE");
			tag.setDouble("storage", reactor.getEnergyStored());
			tag.setDouble("maxStorage", reactor.getEnergyCapacity());
			return tag;
		}
		if (te instanceof TileEntityTurbinePartBase) {
			MultiblockTurbine turbine = ((TileEntityTurbinePartBase) te).getTurbine();
			if (turbine == null)
				return null;

			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("euType", "FE");
			tag.setDouble("storage", turbine.getEnergyStored());
			tag.setDouble("maxStorage", turbine.getEnergyCapacity());
			return tag;
		}
		return null;
	}

	@Override
	public int getReactorHeat(World world, BlockPos pos) {
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
		if (te instanceof TileEntityReactorPartBase) {
			MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
			if (reactor == null)
				return null;

			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 1);
			tag.setBoolean("reactorPoweredB", reactor.getActive());
			tag.setBoolean("cooling", reactor.isPassivelyCooled());
			tag.setString("system", reactor.getPowerSystem().unitOfMeasure);
			tag.setDouble("heat", (double) reactor.getFuelHeat());
			tag.setInteger("coreHeat", (int) reactor.getReactorHeat());
			tag.setDouble("storage", (double) reactor.getEnergyStored());
			tag.setDouble("capacity", (double) reactor.getEnergyCapacity());
			tag.setDouble("output", (double) reactor.getEnergyGeneratedLastTick());
			tag.setInteger("rods", reactor.getFuelRodCount());
			tag.setInteger("fuel", reactor.getFuelAmount());
			tag.setInteger("waste", reactor.getWasteAmount());
			tag.setInteger("fuelCapacity", reactor.getCapacity());
			tag.setDouble("consumption", (double) reactor.getFuelConsumedLastTick());
			BlockPos min = reactor.getMinimumCoord();
			BlockPos max = reactor.getMaximumCoord();
			tag.setString("size", String.format("%sx%sx%s",max.getX() - min.getX() + 1, max.getY() - min.getY() + 1, max.getZ() - min.getZ() + 1));
			return tag;
		}
		if (te instanceof TileEntityTurbinePartBase) {
			MultiblockTurbine turbine = ((TileEntityTurbinePartBase) te).getTurbine();
			if (turbine == null)
				return null;

			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", 2);
			tag.setBoolean("reactorPoweredB", turbine.getActive());
			tag.setString("system", turbine.getPowerSystem().unitOfMeasure);
			tag.setDouble("storage", (double) turbine.getEnergyStored());
			tag.setDouble("capacity", (double) turbine.getEnergyCapacity());
			tag.setDouble("output", (double) turbine.getEnergyGeneratedLastTick());
			tag.setDouble("speed", (double) turbine.getRotorSpeed());
			tag.setDouble("speedMax", (double) turbine.getMaxRotorSpeed());
			tag.setDouble("efficiency", (double) turbine.getRotorEfficiencyLastTick());
			tag.setDouble("consumption", (double) turbine.getFluidConsumedLastTick());
			tag.setInteger("blades", turbine.getNumRotorBlades());
			tag.setInteger("mass", turbine.getRotorMass());
			BlockPos min = turbine.getMinimumCoord();
			BlockPos max = turbine.getMaximumCoord();
			tag.setString("size", String.format("%sx%sx%s",max.getX() - min.getX() + 1, max.getY() - min.getY() + 1, max.getZ() - min.getZ() + 1));
			return tag;
		}
		return null;
	}
}
