package com.zuxelus.energycontrol.crossmod;

import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;

public class CrossModBase {

	public CompoundTag getEnergyData(BlockEntity te) {
		return null;
	}

	public CompoundTag getCardData(BlockEntity te) {
		return null;
	}

	public CompoundTag getInventoryData(BlockEntity te) {
		return null;
	}

	public int getReactorHeat(Level world, BlockPos pos) {
		return -1;
	}

	public IEnergyStorage getEnergyStorage(BlockEntity te) {
		return null;
	}

	public List<FluidInfo> getAllTanks(BlockEntity te) {
		return null;
	}

	public IFluidTank getPipeTank(BlockEntity te) {
		return null;
	}
}
