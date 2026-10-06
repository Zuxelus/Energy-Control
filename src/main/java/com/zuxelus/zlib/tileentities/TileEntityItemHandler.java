package com.zuxelus.zlib.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

// LibBlockAttributes is not available for 1.20.4; other mods see the vanilla Inventory from TileEntityInventory
public abstract class TileEntityItemHandler extends TileEntityInventory {

	public TileEntityItemHandler(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
