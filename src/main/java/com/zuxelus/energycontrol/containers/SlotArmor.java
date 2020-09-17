package com.zuxelus.energycontrol.containers;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class SlotArmor extends Slot {
	private final int armorType;

	public SlotArmor(InventoryPlayer inventory, int armorType, int x, int y) {
		super(inventory, 36 + 3 - armorType, x, y);
		this.armorType = armorType;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack == null)
			return false;
		Item item = stack.getItem();
		if (item == null)
			return false;
		return item.isValidArmor(stack, armorType, ((InventoryPlayer) inventory).player);
	}
}
