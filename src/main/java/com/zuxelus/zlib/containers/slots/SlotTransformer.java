package com.zuxelus.zlib.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotTransformer extends SlotFilter {

	public SlotTransformer(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() {
		return "zlib:slots/slot_transformer";
	}

	@Override
	public int getSlotStackLimit() {
		return 3;
	}
}

