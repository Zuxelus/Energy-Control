package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.items.cards.MainCardItem;

import net.minecraft.item.ItemStack;

public class CardHolderInventory extends InventoryItem {

	public CardHolderInventory(ItemStack parent) {
		super(parent);
	}

	@Override
	public int getInvSize() {
		return 54;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack) {
		return stack.getItem() instanceof MainCardItem;
	}
}
