package com.zuxelus.energycontrol.containers;

import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class BaseContainer<T extends Inventory> extends Container {
	public final T be;

	protected BaseContainer(int syncId, PlayerInventory playerInventory, T inventory) {
		super(null, syncId);
		this.be = inventory;
		inventory.onInvOpen(playerInventory.player);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return be.canPlayerUseInv(player);
	}

	protected void addPlayerInventorySlots(PlayerEntity player, int height) {
		addPlayerInventorySlots(player, 178, height);
	}

	protected void addPlayerInventorySlots(PlayerEntity player, int width, int height) {
		int xStart = (width - 162) / 2;
		for (int row = 0; row < 3; row++)
			for (int i = 0; i < 9; i++)
				addSlot(new Slot(player.inventory, i + row * 9 + 9, xStart + i * 18, height - 82 + row * 18));

		addPlayerInventoryTopSlots(player, xStart, height);
	}

	protected void addPlayerInventoryTopSlots(PlayerEntity player, int width, int height) {
		for (int col = 0; col < 9; col++)
			addSlot(new Slot(player.inventory, col, width + col * 18, height - 24));
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
}
