package com.zuxelus.energycontrol.containers;

import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

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
