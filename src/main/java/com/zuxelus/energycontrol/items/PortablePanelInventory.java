package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.items.cards.MainCardItem;

import net.minecraft.item.ItemStack;

public class PortablePanelInventory extends InventoryItem {
	public static final byte SLOT_CARD = 0;
	public static final byte SLOT_UPGRADE_RANGE = 1;

	public PortablePanelInventory(ItemStack parent) {
		super(parent);
	}

	@Override
	public int getInvSize() {
		return 2;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack) { // ISlotItemFilter
		switch (slot) {
		case SLOT_CARD:
			return stack.getItem() instanceof MainCardItem;
		case SLOT_UPGRADE_RANGE:
			return stack.getItem() instanceof UpgradeRangeItem;
		default:
			return false;
		}
	}
}
