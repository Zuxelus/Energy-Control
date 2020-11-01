package com.zuxelus.energycontrol.tileentities;

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
				PositionedSoundRecord sound = new PositionedSoundRecord(new SoundEvent(new ResourceLocation(name)), SoundCategory.MASTER, (float) volume, 1.0F, (float) x, (float) y, (float) z);
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
		return sound == null ? false : Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(sound);
	}
}
