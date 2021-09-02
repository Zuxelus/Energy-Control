package com.zuxelus.energycontrol.containers.slots;

import com.mojang.datafixers.util.Pair;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlotRange extends SlotFilter {

	public SlotRange(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
		return Pair.of(PlayerContainer.BLOCK_ATLAS, new ResourceLocation(EnergyControl.MODID + ":slots/slot_range"));
	}
}
