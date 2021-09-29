package com.zuxelus.energycontrol.crossmod;

import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrossModBase {

	public CompoundNBT getEnergyData(TileEntity te) {
		return null;
	}

	public List<FluidInfo> getAllTanks(TileEntity te) {
		return null;
	}

	public CompoundNBT getCardData(TileEntity te) {
		return null;
	}

	public CompoundNBT getInventoryData(TileEntity te) {
		return null;
	}

	public int getReactorHeat(World world, BlockPos pos) {
		return -1;
	}
}
