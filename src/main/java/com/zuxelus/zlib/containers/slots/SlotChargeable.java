package com.zuxelus.zlib.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.IIcon;

public class SlotChargeable extends SlotFilter {
	public static IIcon slotIcon;

	public SlotChargeable(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public IIcon getBackgroundIconIndex() {
		return slotIcon;
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}

