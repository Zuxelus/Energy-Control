package com.zuxelus.zlib.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotChargeable extends SlotFilter {

	public SlotChargeable(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() {
		return "zlib:slots/slot_chargeable";
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}

