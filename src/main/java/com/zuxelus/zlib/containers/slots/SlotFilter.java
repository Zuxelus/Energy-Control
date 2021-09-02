package com.zuxelus.zlib.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotFilter extends Slot {

	public SlotFilter(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack itemStack) {
		if (container instanceof ISlotItemFilter)
			return ((ISlotItemFilter) container).isItemValid(getSlotIndex(), itemStack);
		return super.mayPlace(itemStack);
	}
}
