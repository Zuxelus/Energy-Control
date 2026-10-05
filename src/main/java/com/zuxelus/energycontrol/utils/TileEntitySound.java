package com.zuxelus.energycontrol.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class TileEntitySound {
	private SoundInstance sound;

	public TileEntitySound() { }

	public void playAlarm(double x, double y, double z, String name, float range) {
		Vec3d person = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
		if (person != null) {
			double volume = 1.0F - Math.sqrt(person.squaredDistanceTo(x, y, z) / range / range);
			if (volume > 0) {
				if (volume < 0.3)
					volume = 0.3;
				sound = new PositionedSoundInstance(new SoundEvent(new Identifier(name)), SoundCategory.MASTER, (float) volume, 1.0F, (float) person.x, (float) person.y, (float) person.z);
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
		return sound == null ? false : MinecraftClient.getInstance().getSoundManager().isPlaying(sound);
	}
}

