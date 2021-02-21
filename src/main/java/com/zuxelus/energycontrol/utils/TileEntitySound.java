package com.zuxelus.energycontrol.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class TileEntitySound {
	private static final float DEFAULT_RANGE = 16F;
	private SimpleSound sound;

	public TileEntitySound() { }

	public void stopAlarm() {
		if (sound != null) {
			Minecraft.getInstance().getSoundHandler().stop(sound);
			sound = null;
		}
	}

	public void playAlarm(double x, double y, double z, String soundName, float range, boolean skipCheck) {
		if (sound == null || skipCheck)
			sound = playAlarm(x, y, z, soundName, range);
	}

	public boolean isPlaying() {
		if (sound == null)
			return false;
		return Minecraft.getInstance().getSoundHandler().isPlaying(sound);
	}

	public SimpleSound playAlarm(double x, double y, double z, String name, float volume) {
		float range = DEFAULT_RANGE;

		if (volume > 1.0F)
			range *= volume;

		Entity person = Minecraft.getInstance().getRenderViewEntity();

		if (person != null && volume > 0 && person.getDistanceSq(x, y, z) < range * range) {
			SimpleSound sound = new SimpleSound(new SoundEvent(new ResourceLocation(name)), SoundCategory.MASTER, volume, 1.0F, (float) x, (float) y, (float) z);
			Minecraft.getInstance().getSoundHandler().play(sound);
			return sound;
		}
		return null;
	}
}
