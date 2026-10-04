package com.zuxelus.zlib.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

// LibBlockAttributes is not available for 1.20.4; other mods see the vanilla Inventory from TileEntityInventory
public abstract class TileEntityItemHandler extends TileEntityInventory {

	public TileEntityItemHandler(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
