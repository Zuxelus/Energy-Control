package com.zuxelus.energycontrol.crossmod;

import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CrossAdvGenerators extends CrossModBase {

	@Override
	public CompoundTag getEnergyData(BlockEntity te) {
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(BlockEntity te) {
		return null;
	}

	@Override
	public CompoundTag getCardData(BlockEntity te) {
		return null;
	}
}
