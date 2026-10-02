package com.zuxelus.zlib.containers.slots;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;

public class SlotTransformer extends SlotFilter {

	public SlotTransformer(Container inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public com.mojang.datafixers.util.Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return com.mojang.datafixers.util.Pair.of(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS, ResourceLocation.parse("zlib:slots/slot_transformer"));
    }

	@Override
	public int getMaxStackSize() {
		return 3;
	}
}

