package com.zuxelus.energycontrol.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class BlockEntitySound {
	private static final float DEFAULT_RANGE = 16F;
	private PositionedSoundInstance sound;

	public BlockEntitySound() { }

	public void stopAlarm() {
		if (sound != null) {
			MinecraftClient.getInstance().getSoundManager().stop(sound);
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
		return MinecraftClient.getInstance().getSoundManager().isPlaying(sound);
	}

	public PositionedSoundInstance playAlarm(double x, double y, double z, String name, float volume) {
		float range = DEFAULT_RANGE;

		if (volume > 1.0F)
			range *= volume;

		Entity person = MinecraftClient.getInstance().getCameraEntity();

		if (person != null && volume > 0 && person.squaredDistanceTo(x, y, z) < range * range) {
			PositionedSoundInstance sound = new PositionedSoundInstance(new SoundEvent(new Identifier(name)), SoundCategory.MASTER, volume, 1.0F, (float) x, (float) y, (float) z);
			MinecraftClient.getInstance().getSoundManager().play(sound);
			return sound;
		}
		return null;
	}
}
