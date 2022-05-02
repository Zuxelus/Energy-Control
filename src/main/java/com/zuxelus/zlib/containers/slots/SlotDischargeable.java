package com.zuxelus.zlib.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.IIcon;

public class SlotDischargeable extends SlotFilter {
	public static IIcon[] slotIcons;
	private int tier;

	public SlotDischargeable(IInventory inventory, int slotIndex, int x, int y, int tier) {
		super(inventory, slotIndex, x, y);
		this.tier = tier;
	}

	@Override
	public IIcon getBackgroundIconIndex() {
		return tier > 3 ? slotIcons[2] : slotIcons[tier - 1];
	}
}
