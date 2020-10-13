package com.zuxelus.energycontrol.screen.handlers;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SlotFilter extends Slot {
	private final int slotIndex;

	public SlotFilter(Inventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
		this.slotIndex = slotIndex;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		if (inventory instanceof ISlotItemFilter)
			return ((ISlotItemFilter) inventory).canInsert(slotIndex, stack);
		return super.canInsert(stack);
	}
}
