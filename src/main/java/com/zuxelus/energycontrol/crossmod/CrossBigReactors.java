package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort.Direction;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPartBase;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class CrossBigReactors extends CrossModBase {

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
	public List<IFluidTank> getAllTanks(TileEntity te) {
		if (te instanceof TileEntityReactorPartBase) {
			MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
			if (reactor == null)
				return null;
			if (reactor.isPassivelyCooled())
				return null;
			FluidTankInfo[] info = reactor.getCoolantContainer().getTankInfo(-1);
			List<IFluidTank> result = new ArrayList<>();
			for (FluidTankInfo tank : info)
				result.add(new FluidTank(tank.fluid, tank.capacity));
			return result;
		}
		if (te instanceof TileEntityTurbinePartBase) {
			MultiblockTurbine turbine = ((TileEntityTurbinePartBase) te).getTurbine();
			if (turbine == null)
				return null;

			IFluidHandler input = turbine.getFluidHandler(Direction.Input);
			IFluidHandler output = turbine.getFluidHandler(Direction.Output);
			List<IFluidTank> result = new ArrayList<>();
			result.add(new FluidTank(input.getTankProperties()[0].getContents(), input.getTankProperties()[0].getCapacity()));
			result.add(new FluidTank(output.getTankProperties()[0].getContents(), output.getTankProperties()[0].getCapacity()));
			return result;
		}
		return null;
	}
}
