package com.zuxelus.zlib.containers.slots;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;

public class SlotDischargeable extends SlotFilter {

	public SlotDischargeable(Container inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Identifier getNoItemIcon() {
		return Identifier.parse("zlib:slots/slot_dischargeable");
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}

