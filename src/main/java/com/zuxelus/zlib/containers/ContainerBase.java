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
			if (stack != null && stack.getItem() == filter)
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
		return te.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		Slot slot = (Slot) this.inventorySlots.get(slotId);
		if (slot == null || !slot.getHasStack())
			return null;

		ItemStack items = slot.getStack();
		ItemStack stack = items.copy();
		if (slotId < te.getSizeInventory()) { // moving from panel to inventory
			if (!mergeItemStack(items, te.getSizeInventory(), inventorySlots.size(), true))
				return null;
		} else // moving from inventory to panel
			if (!mergeItemStack(items, 0, te.getSizeInventory(), false))
				return null;
		if (items.stackSize == 0)
			slot.putStack(null);
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
			while (stack.stackSize > 0 && (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex)) {
				Slot slot = (Slot) inventorySlots.get(i);
				ItemStack itemstack = slot.getStack();

				if (itemstack != null && itemstack.getItem() == stack.getItem()
						&& (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(stack, itemstack) && slot.isItemValid(stack)) {
					int j = itemstack.stackSize + stack.stackSize;
					int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
					if (j <= maxSize) {
						stack.stackSize = 0;
						itemstack.stackSize = j;
						slot.onSlotChanged();
						flag = true;
					} else if (itemstack.stackSize < maxSize) {
						stack.stackSize -= maxSize - itemstack.stackSize;
						itemstack.stackSize = maxSize;
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

		if (stack.stackSize > 0) {
			if (reverseDirection)
				i = endIndex - 1;
			else
				i = startIndex;

			while (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex) {
				Slot slot = (Slot) inventorySlots.get(i);
				ItemStack itemstack = slot.getStack();

				if (itemstack == null && slot.isItemValid(stack)) {
					if (stack.stackSize > slot.getSlotStackLimit())
						slot.putStack(stack.splitStack(slot.getSlotStackLimit()));
					else
						slot.putStack(stack.splitStack(stack.stackSize));
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
