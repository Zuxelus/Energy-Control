package com.zuxelus.energycontrol.containers;

import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BaseContainer<T extends Inventory> extends Container {
	public final T be;

	protected BaseContainer(int syncId, PlayerInventory playerInventory, T be) {
		super(null, syncId);
		this.be = be;
		be.onInvOpen(playerInventory.player);
	}

	protected void addPlayerInventorySlots(PlayerEntity player, int height) {
		addPlayerInventorySlots(player, 178, height);
	}

	protected void addPlayerInventorySlots(PlayerEntity player, int width, int height) {
		addPlayerInventorySlots(player, width, height, null);
	}

	protected void addPlayerInventorySlots(PlayerEntity player, int height, Item filter) {
		addPlayerInventorySlots(player, 178, height, filter);
	}

	protected void addPlayerInventorySlots(PlayerEntity player, int width, int height, Item filter) {
		int xStart = (width - 162) / 2;
		for (int row = 0; row < 3; row++)
			for (int i = 0; i < 9; i++)
				addSlot(new Slot(player.inventory, i + row * 9 + 9, xStart + i * 18, height - 82 + row * 18));

		addPlayerInventoryTopSlots(player, xStart, height, filter);
	}

	protected void addPlayerInventoryTopSlots(PlayerEntity player, int width, int height, Item filter) {
		for (int col = 0; col < 9; col++) {
			ItemStack stack = player.inventory.getInvStack(col);
			if (!stack.isEmpty() && stack.getItem() == filter)
				addSlot(new Slot(player.inventory, col, width + col * 18, height - 24) {
					@Override
					public boolean canInsert(ItemStack stack) {
						return false;
					}

					@Override
					public boolean canTakeItems(PlayerEntity playerEntity) {
						return false;
					}
				});
			else
				addSlot(new Slot(player.inventory, col, width + col * 18, height - 24));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return be.canPlayerUseInv(player);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack originalStack = slot.getStack();
			newStack = originalStack.copy();
			if (invSlot < be.getInvSize()) {
				if (!this.insertItem(originalStack, be.getInvSize(), this.slots.size(), true))
					return ItemStack.EMPTY;
			} else if (!this.insertItem(originalStack, 0, be.getInvSize(), false))
				return ItemStack.EMPTY;

			if (originalStack.isEmpty())
				slot.setStack(ItemStack.EMPTY);
			else
				slot.markDirty();
		}
		return newStack;
	}

	@Override
	protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
		boolean flag = false;
		int i = startIndex;
		if (fromLast)
			i = endIndex - 1;

		if (stack.isStackable()) {
			while (!stack.isEmpty() && (!fromLast && i < endIndex || fromLast && i >= startIndex)) {
				Slot slot = slots.get(i);
				ItemStack itemstack = slot.getStack();

				if (!itemstack.isEmpty() && canStacksCombine(stack, itemstack) && slot.canInsert(stack)) {
					int j = itemstack.getCount() + stack.getCount();
					int maxSize = Math.min(slot.getMaxStackAmount(), stack.getMaxCount());
					if (j <= maxSize) {
						stack.setCount(0);
						itemstack.setCount(j);
						slot.markDirty();
						flag = true;
					} else if (itemstack.getCount() < maxSize) {
						stack.decrement(maxSize - itemstack.getCount());
						itemstack.setCount(maxSize);
						slot.markDirty();
						flag = true;
					}
				}

				if (fromLast)
					--i;
				else
					++i;
			}
		}

		if (!stack.isEmpty()) {
			if (fromLast)
				i = endIndex - 1;
			else
				i = startIndex;

			while (!fromLast && i < endIndex || fromLast && i >= startIndex) {
				Slot slot = slots.get(i);
				ItemStack itemstack = slot.getStack();

				if (itemstack.isEmpty() && slot.canInsert(stack)) {
					if (stack.getCount() > slot.getMaxStackAmount())
						slot.setStack(stack.split(slot.getMaxStackAmount()));
					else
						slot.setStack(stack.split(stack.getCount()));
					slot.markDirty();
					flag = true;
					break;
				}

				if (fromLast)
					--i;
				else
					++i;
			}
		}
		return flag;
	}
}
