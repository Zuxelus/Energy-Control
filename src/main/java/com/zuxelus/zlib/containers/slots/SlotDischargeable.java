package com.zuxelus.zlib.containers.slots;

import com.mojang.datafixers.util.Pair;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;

public class SlotDischargeable extends SlotFilter {

	public SlotDischargeable(Container inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
		return Pair.of(InventoryMenu.BLOCK_ATLAS, ResourceLocation.parse("zlib:slots/slot_dischargeable"));
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}

