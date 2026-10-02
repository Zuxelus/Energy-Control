package com.zuxelus.zlib.containers.slots;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;

public class SlotDischargeable extends SlotFilter {

	public SlotDischargeable(Container inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public com.mojang.datafixers.util.Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return com.mojang.datafixers.util.Pair.of(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS, ResourceLocation.parse("zlib:slots/slot_dischargeable"));
    }

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}

