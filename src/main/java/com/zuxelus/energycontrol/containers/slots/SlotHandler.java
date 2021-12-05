package com.zuxelus.energycontrol.containers.slots;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = EnergyControl.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SlotHandler {
	private static final String BLOCK_ATLAS = "minecraft:textures/atlas/blocks.png";

	@SubscribeEvent
	public static void loadTextures(TextureStitchEvent.Pre event) {
		if (!event.getAtlas().location().toString().equals(BLOCK_ATLAS)) {
			return;
		}
		registerTexture(event, EnergyControl.MODID + ":slots/slot_card");
		registerTexture(event, EnergyControl.MODID + ":slots/slot_color");
		registerTexture(event, EnergyControl.MODID + ":slots/slot_range");
		registerTexture(event, EnergyControl.MODID + ":slots/slot_touch");
		registerTexture(event, EnergyControl.MODID + ":slots/slot_power");
		//registerTexture(event, "zlib:slots/slot_chargeable");
		registerTexture(event, "zlib:slots/slot_dischargeable");
	}

	private static void registerTexture(TextureStitchEvent.Pre event, String texture) {
		event.addSprite(new ResourceLocation(texture));
	}
}
