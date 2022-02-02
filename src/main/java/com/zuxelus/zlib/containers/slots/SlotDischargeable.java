package com.zuxelus.zlib.containers.slots;

import com.mojang.datafixers.util.Pair;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlotDischargeable extends SlotFilter {

	public SlotDischargeable(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
		return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("zlib:slots/slot_dischargeable"));
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}

