package com.zuxelus.energycontrol.crossmod;

import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrossModBase {

	public NbtCompound getEnergyData(BlockEntity te) {
		return null;
	}

	public List<FluidInfo> getAllTanks(BlockEntity te) {
		return null;
	}

	public NbtCompound getCardData(BlockEntity te) {
		return null;
	}

	public NbtCompound getInventoryData(BlockEntity te) {
		return null;
	}

	public int getReactorHeat(World world, BlockPos pos) {
		return -1;
	}
}
