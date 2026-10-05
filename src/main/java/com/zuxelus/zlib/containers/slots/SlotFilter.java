package com.zuxelus.zlib.containers.slots;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SlotFilter extends Slot {

	private final int slotIndex; // Slot.index is private in 1.16

	public SlotFilter(Inventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
		this.slotIndex = slotIndex;
	}

	@Override
	public boolean canInsert(ItemStack itemStack) {
		if (inventory instanceof ISlotItemFilter)
			return ((ISlotItemFilter) inventory).isItemValid(slotIndex, itemStack);
		return super.canInsert(itemStack);
	}
}
