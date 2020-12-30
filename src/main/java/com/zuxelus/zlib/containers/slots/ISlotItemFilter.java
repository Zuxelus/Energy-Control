package com.zuxelus.zlib.containers.slots;

import net.minecraft.item.ItemStack;

public interface ISlotItemFilter {
	boolean isItemValid(int slotIndex, ItemStack itemStack);
}
