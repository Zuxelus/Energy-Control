package com.zuxelus.zlib.containers.slots;

import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlotTransformer extends SlotFilter {

	public SlotTransformer(Container inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
		return Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("zlib:slots/slot_transformer"));
	}

	@Override
	public int getMaxStackSize() {
		return 3;
	}
}
