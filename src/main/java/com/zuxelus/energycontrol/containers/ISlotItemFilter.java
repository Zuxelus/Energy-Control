package com.zuxelus.energycontrol.containers;

import net.minecraft.item.ItemStack;

public interface ISlotItemFilter {
	boolean canInsert(int slotIndex, ItemStack itemStack);
}
