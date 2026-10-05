package com.zuxelus.zlib.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

// exposed to other mods through the Fabric Transfer API: ItemStorage.SIDED wraps any vanilla Inventory, honoring isValid
public abstract class TileEntityItemHandler extends TileEntityInventory {

	public TileEntityItemHandler(BlockEntityType<?> type) {
		super(type);
	}
}
