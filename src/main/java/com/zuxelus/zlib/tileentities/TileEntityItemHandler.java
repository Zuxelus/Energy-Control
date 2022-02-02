package com.zuxelus.zlib.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class TileEntityItemHandler extends TileEntityInventory implements IItemHandler {

	public TileEntityItemHandler(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public int getSlots() {
		return getSizeInventory();
	}

	/*@Override // In 1.15.2 IItemHandler.getStackInSlot = IInventory.getStackInSlot
	public ItemStack getStackInSlot(int slot) {
		return getStackInSlot(slot);
	}*/

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		if (!isItemValid(slot, stack))
			return stack;

		ItemStack existing = getStackInSlot(slot);
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;
			limit -= existing.getCount();
		}
		if (limit <= 0)
			return stack;

		boolean reachedLimit = stack.getCount() > limit;
		if (!simulate) {
			if (existing.isEmpty())
				inventory.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			else
				existing.grow(reachedLimit ? limit : stack.getCount());
			// onContentsChanged(slot);
		}
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0)
			return ItemStack.EMPTY;

		ItemStack existing = inventory.get(slot);
		if (existing.isEmpty())
			return ItemStack.EMPTY;

		int toExtract = Math.min(amount, existing.getMaxStackSize());
		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				inventory.set(slot, ItemStack.EMPTY);
				// onContentsChanged(slot);
				return existing;
			}
			return existing.copy();
		}
		if (!simulate) {
			inventory.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
			// onContentsChanged(slot);
		}
		return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
	}

	@Override
	public int getSlotLimit(int slot) {
		return getInventoryStackLimit();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return isItemValidForSlot(slot, stack);
	}
}
