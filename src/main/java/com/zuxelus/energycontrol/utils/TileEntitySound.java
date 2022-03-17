package com.zuxelus.energycontrol.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class TileEntitySound {
	private SoundInstance sound;

	public TileEntitySound() { }

	public void playAlarm(double x, double y, double z, String name, float range) {
		Vec3 person = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		if (person != null) {
			double volume = 1.0F - Math.sqrt(person.distanceToSqr(x, y, z) / range / range);
			if (volume > 0) {
				if (volume < 0.3)
					volume = 0.3;
				sound = new SimpleSoundInstance(new SoundEvent(new ResourceLocation(name)), SoundSource.MASTER, (float) volume, 1.0F, (float) person.x, (float) person.y, (float) person.z);
				Minecraft.getInstance().getSoundManager().play(sound);
				return;
			}
		}
		sound = null;
	}

	public void stopAlarm() {
		if (sound != null) {
			Minecraft.getInstance().getSoundManager().stop(sound);
			sound = null;
		}
	}

	public boolean isPlaying() {
		return sound != null && Minecraft.getInstance().getSoundManager().isActive(sound);
	}
}
