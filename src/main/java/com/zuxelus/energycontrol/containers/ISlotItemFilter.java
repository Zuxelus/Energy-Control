package com.zuxelus.energycontrol.containers;

import net.minecraft.item.ItemStack;

public interface ISlotItemFilter {

	boolean isItemValid(int slotIndex, ItemStack stack);
}
