package com.zuxelus.energycontrol.containers;

import net.minecraft.item.ItemStack;

public interface ISlotItemFilter {
	boolean canInsert(int slot, ItemStack stack);
}
