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

		for (int col = 0; col < 9; col++)
			addSlotToContainer(new Slot(player.inventory, col, xStart + col * 18, height - 24));		
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
		int initialCount = items.stackSize;
		if (slotId < te.getSizeInventory()) { // moving from panel to inventory
			mergeItemStack(items, te.getSizeInventory(), inventorySlots.size(), false);
			if (items.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
				if (initialCount != items.stackSize)
					return items;
			}
		} else { // moving from inventory to panel
			for (int i = 0; i < te.getSizeInventory(); i++) {
				if (!te.isItemValidForSlot(i, items))
					continue;
				ItemStack targetStack = te.getStackInSlot(i);
				if (targetStack == null) {
					Slot targetSlot = (Slot) this.inventorySlots.get(i);
					targetSlot.putStack(items);
					slot.putStack((ItemStack) null);
					break;
				} else if (items.isStackable() && items.isItemEqual(targetStack)) {
					mergeItemStack(items, i, i + 1, false);
					if (items.stackSize == 0) {
						slot.putStack((ItemStack) null);
					} else {
						slot.onSlotChanged();
						if (initialCount != items.stackSize)
							return items;
					}
					break;
				}

			}
		}
		return null;
	}
}
