package com.zuxelus.zlib.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class TileEntityItemHandler extends TileEntityInventory implements IItemHandler {

	public TileEntityItemHandler(String name) {
		super(name);
	}

	@Override
	public int getSlots() {
		return getSizeInventory();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack == null)
			return null;
		if (!isItemValid(slot, stack))
			return stack;

		ItemStack existing = getStackInSlot(slot);
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

		if (existing != null) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;
			limit -= existing.stackSize;
		}
		if (limit <= 0)
			return stack;

		boolean reachedLimit = stack.stackSize > limit;
		if (!simulate) {
			if (existing == null)
				inventory[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
			else
				existing.stackSize += reachedLimit ? limit : stack.stackSize;
			// onContentsChanged(slot);
		}
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0)
			return null;

		ItemStack existing = getStackInSlot(slot);
		if (existing == null)
			return null;

		int toExtract = Math.min(amount, existing.getMaxStackSize());
		if (existing.stackSize <= toExtract) {
			if (!simulate) {
				inventory[slot] = null;
				// onContentsChanged(slot);
				return existing;
			}
			return existing.copy();
		}
		if (!simulate) {
			inventory[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract);
			// onContentsChanged(slot);
		}
		return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
	}

	public int getSlotLimit(int slot) { // In 1.10 not in IItemHandler
		return getInventoryStackLimit();
	}

	public boolean isItemValid(int slot, ItemStack stack) { // In 1.10 not in IItemHandler
		return isItemValidForSlot(slot, stack);
	}
}
