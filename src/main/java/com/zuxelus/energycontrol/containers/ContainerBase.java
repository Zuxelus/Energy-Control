package com.zuxelus.energycontrol.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
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
		int xStart = (width - 162) / 2;
		for (int row = 0; row < 3; row++)
			for (int i = 0; i < 9; i++)
				addSlotToContainer(new Slot(player.inventory, i + row * 9 + 9, xStart + i * 18, height - 82 + row * 18));

		addPlayerInventoryTopSlots(player, xStart, height);
	}

	protected void addPlayerInventoryTopSlots(EntityPlayer player, int width, int height) {
		for (int col = 0; col < 9; col++)
			addSlotToContainer(new Slot(player.inventory, col, width + col * 18, height - 24));
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
}
