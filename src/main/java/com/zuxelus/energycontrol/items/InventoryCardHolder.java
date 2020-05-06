package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.items.cards.ItemCardMain;

import net.minecraft.item.ItemStack;

public class InventoryCardHolder extends ItemInventory {

	public InventoryCardHolder(ItemStack parent, String name) {
		super(parent, name);
	}

	@Override
	public int getSizeInventory() {
		return 54;
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) {
		return stack.getItem() instanceof ItemCardMain;
	}
}
