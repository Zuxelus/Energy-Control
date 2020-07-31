package com.zuxelus.energycontrol.crossmod.bigreactors;

import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort.Direction;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPartBase;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class BigReactors extends CrossBigReactors {

	@Override
	public IFluidTankProperties[] getAllTanks(TileEntity te) {
		if (te instanceof TileEntityReactorPartBase) {
			MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
			if (reactor == null)
				return null;
			if (reactor.isPassivelyCooled())
				return null;
			return FluidTankProperties.convert(reactor.getCoolantContainer().getTankInfo(-1));
		}
		if (te instanceof TileEntityTurbinePartBase) {
			MultiblockTurbine turbine = ((TileEntityTurbinePartBase) te).getTurbine();
			if (turbine == null)
				return null;

			IFluidTankProperties[] result = new IFluidTankProperties[] {
					turbine.getFluidHandler(Direction.Input).getTankProperties()[0],
					turbine.getFluidHandler(Direction.Output).getTankProperties()[0] };
			return result;
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
}
