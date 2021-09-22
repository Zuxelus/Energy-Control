package com.zuxelus.energycontrol.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class TileEntitySound {
	private static final float DEFAULT_RANGE = 16F;
	private SimpleSoundInstance sound;

	public TileEntitySound() { }

	public void stopAlarm() {
		if (sound != null) {
			Minecraft.getInstance().getSoundManager().stop(sound);
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
		return Minecraft.getInstance().getSoundManager().isActive(sound);
	}

	public SimpleSoundInstance playAlarm(double x, double y, double z, String name, float volume) {
		float range = DEFAULT_RANGE;

		if (volume > 1.0F)
			range *= volume;

		Entity person = Minecraft.getInstance().getCameraEntity();

		if (person != null && volume > 0 && person.distanceToSqr(x, y, z) < range * range) {
			SimpleSoundInstance sound = new SimpleSoundInstance(new SoundEvent(new ResourceLocation(name)), SoundSource.MASTER, volume, 1.0F, (float) x, (float) y, (float) z);
			Minecraft.getInstance().getSoundManager().play(sound);
			return sound;
		}
		return null;
	}
}
