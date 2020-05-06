package com.zuxelus.energycontrol.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotFilter extends Slot {

	public SlotFilter(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemStack) {
		if (inventory instanceof ISlotItemFilter)
			return ((ISlotItemFilter) inventory).isItemValid(getSlotIndex(), itemStack);
		return super.isItemValid(itemStack);
	}
}
