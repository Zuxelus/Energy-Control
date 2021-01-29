package com.zuxelus.zlib.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotDischargeable extends SlotFilter {
	private int tier;

	public SlotDischargeable(IInventory inventory, int slotIndex, int x, int y, int tier) {
		super(inventory, slotIndex, x, y);
		this.tier = tier;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() {
		return tier > 3 ? "zlib:slots/slot_dischargeable_2" : String.format("zlib:slots/slot_dischargeable_%s", tier - 1);
	}
}
