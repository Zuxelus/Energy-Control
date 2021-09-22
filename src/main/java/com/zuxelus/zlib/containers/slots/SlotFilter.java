package com.zuxelus.zlib.containers.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotFilter extends Slot {

	public SlotFilter(Container inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack itemStack) {
		if (container instanceof ISlotItemFilter)
			return ((ISlotItemFilter) container).isItemValid(getSlotIndex(), itemStack);
		return super.mayPlace(itemStack);
	}
}
