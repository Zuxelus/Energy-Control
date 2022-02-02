package com.zuxelus.energycontrol.crossmod;

import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;

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

	public IEnergyStorage getEnergyStorage(TileEntity te) {
		return null;
	}

	public IFluidTank getPipeTank(TileEntity te) {
		return null;
	}
}
