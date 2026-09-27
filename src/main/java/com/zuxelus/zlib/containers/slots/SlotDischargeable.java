package com.zuxelus.zlib.containers.slots;


import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;

public class SlotDischargeable extends SlotFilter {

	public SlotDischargeable(Container inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public Identifier getNoItemIcon() {
		return Identifier.parse("zlib:slots/slot_dischargeable");
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}

