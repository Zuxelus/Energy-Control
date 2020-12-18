package com.zuxelus.energycontrol.crossmod.bigreactors;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;

public class BigReactorsNoMod extends CrossBigReactors {

	@Override
	public List<IFluidTank> getAllTanks(TileEntity te) {
		return null;
	}

	@Override
	public int getReactorHeat(World world, BlockPos pos) {
		return -1;
	}
}
