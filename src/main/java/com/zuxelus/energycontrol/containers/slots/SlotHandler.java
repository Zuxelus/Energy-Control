package com.zuxelus.energycontrol.containers.slots;

import java.util.Set;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

public class SlotHandler {
	private static final String BLOCK_ATLAS = "minecraft:textures/atlas/blocks.png";

	public static void loadTextures(SpriteAtlasTexture atlas, Set<Identifier> set) {
		if (!atlas.getId().toString().equals(BLOCK_ATLAS))
			return;

		registerTexture(set, EnergyControl.MODID + ":slots/slot_card");
		registerTexture(set, EnergyControl.MODID + ":slots/slot_color");
		registerTexture(set, EnergyControl.MODID + ":slots/slot_range");
		registerTexture(set, EnergyControl.MODID + ":slots/slot_touch");
		registerTexture(set, EnergyControl.MODID + ":slots/slot_power");
		//registerTexture(set, "zlib:slots/slot_chargeable");
		registerTexture(set, "zlib:slots/slot_dischargeable");
	}

	private static void registerTexture(Set<Identifier> set, String texture) {
		set.add(new Identifier(texture));
	}
}
