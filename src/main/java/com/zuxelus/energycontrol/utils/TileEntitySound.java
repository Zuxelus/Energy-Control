package com.zuxelus.energycontrol.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.client.FMLClientHandler;

public class TileEntitySound {
	private PositionedSoundRecord sound;

	public TileEntitySound() { }

	public void playAlarm(double x, double y, double z, String name, float range) {
		Entity person = FMLClientHandler.instance().getClient().getRenderViewEntity();
		if (person != null) {
			double volume = 1.0F - Math.sqrt(person.getDistanceSq(x, y, z) / range / range);
			if (volume > 0) {
				if (volume < 0.3)
					volume = 0.3;
				sound = new PositionedSoundRecord(new SoundEvent(new ResourceLocation(name)), SoundCategory.MASTER, (float) volume, 1.0F, (float) person.posX, (float) person.posY, (float) person.posZ);
				Minecraft.getMinecraft().getSoundHandler().playSound(sound);
				return;
			}
		}
		sound = null;
	}

	public void stopAlarm() {
		if (sound != null) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
			sound = null;
		}
	}

	public boolean isPlaying() {
		return sound != null && Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(sound);
	}
}
