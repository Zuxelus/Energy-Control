package com.zuxelus.zlib.containers.slots;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Shows the item that opened the menu (portable panel, card holder): it must stay where it is while the menu is open. */
public class SlotLocked extends Slot {

	public SlotLocked(Container inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public boolean mayPickup(Player player) {
		return false;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}
}
