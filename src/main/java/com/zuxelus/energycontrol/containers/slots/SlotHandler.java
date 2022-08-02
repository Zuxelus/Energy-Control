package com.zuxelus.energycontrol.containers.slots;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotHandler {

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void loadTextures(TextureStitchEvent.Pre event) {
		registerTexture(event, EnergyControl.MODID + ":slots/slot_card");
		registerTexture(event, EnergyControl.MODID + ":slots/slot_color");
		registerTexture(event, EnergyControl.MODID + ":slots/slot_range");
		registerTexture(event, EnergyControl.MODID + ":slots/slot_touch");
		registerTexture(event, EnergyControl.MODID + ":slots/slot_power");
		registerTexture(event, "zlib:slots/slot_chargeable");
		registerTexture(event, "zlib:slots/slot_dischargeable_0");
		registerTexture(event, "zlib:slots/slot_dischargeable_1");
		registerTexture(event, "zlib:slots/slot_dischargeable_2");
	}

	private void registerTexture(TextureStitchEvent.Pre event, String texture) {
		event.getMap().registerSprite(new ResourceLocation(texture));
	}
}
