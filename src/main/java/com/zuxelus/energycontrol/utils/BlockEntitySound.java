package com.zuxelus.energycontrol.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class BlockEntitySound {
	private PositionedSoundInstance sound;

	public BlockEntitySound() { }

	public void playAlarm(double x, double y, double z, String name, float range) {
		Entity person = MinecraftClient.getInstance().getCameraEntity();
		if (person != null) {
			double volume = 1 - Math.sqrt(person.squaredDistanceTo(x, y, z) / range / range);
			if (volume > 0) {
				sound = new PositionedSoundInstance(new SoundEvent(new Identifier(name)), SoundCategory.MASTER, (float) volume, 1.0F, (float) x, (float) y, (float) z);
				MinecraftClient.getInstance().getSoundManager().play(sound);
				return;
			}
		}
		sound = null;
	}

	public void stopAlarm() {
		if (sound != null) {
			MinecraftClient.getInstance().getSoundManager().stop(sound);
			sound = null;
		}
	}

	public boolean isPlaying() {
		return sound == null? false : MinecraftClient.getInstance().getSoundManager().isPlaying(sound);
	}
}
