package com.zuxelus.zlib.tileentities;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv.ModifiableFixedItemInv;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public abstract class TileEntityItemHandler extends TileEntityInventory implements ModifiableFixedItemInv {

	public TileEntityItemHandler(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public int getSlotCount() {
		return size();
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return getStack(slot);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return isValid(slot, stack);
	}

	@Override
	public boolean setInvStack(int slot, ItemStack stack, Simulation simulation) {
		if (!isItemValidForSlot(slot, stack))
			return false;
		if (simulation.isAction())
			setStack(slot, stack);
		return true;
	}
}
