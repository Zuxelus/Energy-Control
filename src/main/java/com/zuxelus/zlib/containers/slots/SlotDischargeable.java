package com.zuxelus.zlib.containers.slots;

import com.mojang.datafixers.util.Pair;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class SlotDischargeable extends SlotFilter {

	public SlotDischargeable(Inventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Pair<Identifier, Identifier> getBackgroundSprite() {
		return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier("zlib:slots/slot_dischargeable"));
	}

	@Override
	public int getMaxItemCount() {
		return 1;
	}
}

