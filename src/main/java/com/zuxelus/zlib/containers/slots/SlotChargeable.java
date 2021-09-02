package com.zuxelus.zlib.containers.slots;

import com.mojang.datafixers.util.Pair;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlotChargeable extends SlotFilter {

	public SlotChargeable(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
		return Pair.of(PlayerContainer.BLOCK_ATLAS, new ResourceLocation("zlib:slots/slot_chargeable"));
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}

