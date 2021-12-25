package com.zuxelus.zlib.containers.slots;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SlotFilter extends Slot {

	public SlotFilter(Inventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public boolean canInsert(ItemStack itemStack) {
		if (inventory instanceof ISlotItemFilter)
			return ((ISlotItemFilter) inventory).isItemValid(getIndex(), itemStack);
		return super.canInsert(itemStack);
	}
}
