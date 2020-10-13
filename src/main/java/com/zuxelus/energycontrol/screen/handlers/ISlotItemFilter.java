package com.zuxelus.energycontrol.screen.handlers;

import net.minecraft.item.ItemStack;

public interface ISlotItemFilter {
	boolean canInsert(int slot, ItemStack stack);
}
