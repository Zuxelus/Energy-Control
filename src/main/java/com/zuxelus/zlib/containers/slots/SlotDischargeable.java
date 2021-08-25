package com.zuxelus.zlib.containers.slots;

import com.mojang.datafixers.util.Pair;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlotDischargeable extends SlotFilter {
	private int tier;

	public SlotDischargeable(IInventory inventory, int slotIndex, int x, int y, int tier) {
		super(inventory, slotIndex, x, y);
		this.tier = tier;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Pair<ResourceLocation, ResourceLocation> getBackground() {
		return tier > 3 ? "zlib:slots/slot_dischargeable_2" : String.format("zlib:slots/slot_dischargeable_%s", tier - 1);
	}
}
