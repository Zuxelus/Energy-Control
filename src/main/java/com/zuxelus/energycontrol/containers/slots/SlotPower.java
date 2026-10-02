package com.zuxelus.energycontrol.containers.slots;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;

public class SlotPower extends SlotFilter {

	public SlotPower(Container inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	public int getMaxStackSize() {
		return 16;
	}

	@Override
	public com.mojang.datafixers.util.Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return com.mojang.datafixers.util.Pair.of(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "slots/slot_power"));
    }
}
