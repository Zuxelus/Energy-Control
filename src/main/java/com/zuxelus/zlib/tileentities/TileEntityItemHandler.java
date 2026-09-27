package com.zuxelus.zlib.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TileEntityItemHandler extends TileEntityInventory {

	public TileEntityItemHandler(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public int getSlotLimit(int slot) {
		return getMaxStackSize();
	}

	public boolean isItemValid(int slot, ItemStack stack) {
		return canPlaceItem(slot, stack);
	}
}
