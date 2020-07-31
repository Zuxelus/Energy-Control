package com.zuxelus.energycontrol.crossmod.bigreactors;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class BigReactorsNoMod extends CrossBigReactors {

	@Override
	public IFluidTankProperties[] getAllTanks(TileEntity te) {
		return null;
	}

	@Override
	public int getReactorHeat(World world, BlockPos pos) {
		return 0;
	}
}
