package com.zuxelus.zlib.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ContainerBase<T extends IInventory> extends Container {
	public final T te;

	public ContainerBase(T te) {
		this.te = te;
	}

	protected void addPlayerInventorySlots(EntityPlayer player, int height) {
		addPlayerInventorySlots(player, 178, height);
	}

	protected void addPlayerInventorySlots(EntityPlayer player, int width, int height) {
		addPlayerInventorySlots(player, width, height, null);
	}

	protected void addPlayerInventorySlots(EntityPlayer player, int height, Item filter) {
		addPlayerInventorySlots(player, 178, height, filter);
	}

	protected void addPlayerInventorySlots(EntityPlayer player, int width, int height, Item filter) {
		int xStart = (width - 162) / 2;
		for (int row = 0; row < 3; row++)
			for (int i = 0; i < 9; i++)
				addSlotToContainer(new Slot(player.inventory, i + row * 9 + 9, xStart + i * 18, height - 82 + row * 18));

		addPlayerInventoryTopSlots(player, xStart, height, filter);
	}

	protected void addPlayerInventoryTopSlots(EntityPlayer player, int width, int height, Item filter) {
		for (int col = 0; col < 9; col++) {
			ItemStack stack = player.inventory.getStackInSlot(col);
			if (!stack.isEmpty() && stack.getItem() == filter)
				addSlotToContainer(new Slot(player.inventory, col, width + col * 18, height - 24) {
					@Override
					public boolean isItemValid(ItemStack stack) {
						return false;
					}

					@Override
					public boolean canTakeStack(EntityPlayer player) {
						return false;
					}
				});
			else
				addSlotToContainer(new Slot(player.inventory, col, width + col * 18, height - 24));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return te.isUsableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		Slot slot = this.inventorySlots.get(slotId);
		if (slot == null || !slot.getHasStack())
			return ItemStack.EMPTY;

		ItemStack items = slot.getStack();
		ItemStack stack = items.copy();
		if (slotId < te.getSizeInventory()) { // moving from panel to inventory
			if (!mergeItemStack(items, te.getSizeInventory(), inventorySlots.size(), true))
				return ItemStack.EMPTY;
		} else // moving from inventory to panel
			if (!mergeItemStack(items, 0, te.getSizeInventory(), false))
				return ItemStack.EMPTY;
		if (items.isEmpty())
			slot.putStack(ItemStack.EMPTY);
		else 
			slot.onSlotChanged();
		return stack;
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
		boolean flag = false;
		int i = startIndex;
		if (reverseDirection)
			i = endIndex - 1;

		if (stack.isStackable()) {
			while (!stack.isEmpty() && (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex)) {
				Slot slot = inventorySlots.get(i);
				ItemStack itemstack = slot.getStack();

				if (!itemstack.isEmpty() && itemstack.getItem() == stack.getItem()
						&& (!stack.getHasSubtypes() || stack.getMetadata() == itemstack.getMetadata())
						&& ItemStack.areItemStackTagsEqual(stack, itemstack) && slot.isItemValid(stack)) {
					int j = itemstack.getCount() + stack.getCount();
					int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
					if (j <= maxSize) {
						stack.setCount(0);
						itemstack.setCount(j);
						slot.onSlotChanged();
						flag = true;
					} else if (itemstack.getCount() < maxSize) {
						stack.shrink(maxSize - itemstack.getCount());
						itemstack.setCount(maxSize);
						slot.onSlotChanged();
						flag = true;
					}
				}

				if (reverseDirection)
					--i;
				else
					++i;
			}
		}

		if (!stack.isEmpty()) {
			if (reverseDirection)
				i = endIndex - 1;
			else
				i = startIndex;

			while (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex) {
				Slot slot = inventorySlots.get(i);
				ItemStack itemstack = slot.getStack();

				if (itemstack.isEmpty() && slot.isItemValid(stack)) {
					if (stack.getCount() > slot.getSlotStackLimit())
						slot.putStack(stack.splitStack(slot.getSlotStackLimit()));
					else
						slot.putStack(stack.splitStack(stack.getCount()));
					slot.onSlotChanged();
					flag = true;
					break;
				}

				if (reverseDirection)
					--i;
				else
					++i;
			}
		}
		return flag;
	}
}
