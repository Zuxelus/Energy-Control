package com.zuxelus.energycontrol.containers.slots;

import com.mojang.datafixers.util.Pair;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class SlotRange extends SlotFilter {

	public SlotRange(Inventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Pair<Identifier, Identifier> getBackgroundSprite() {
		return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(EnergyControl.MODID, "slots/slot_range"));
	}
}
