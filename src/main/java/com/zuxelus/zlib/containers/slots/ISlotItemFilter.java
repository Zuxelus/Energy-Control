package com.zuxelus.zlib.containers.slots;

import net.minecraft.world.item.ItemStack;

public interface ISlotItemFilter {

	boolean isItemValid(int slotIndex, ItemStack stack);
}
