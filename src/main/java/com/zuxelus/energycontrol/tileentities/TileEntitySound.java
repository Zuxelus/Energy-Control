package com.zuxelus.energycontrol.tileentities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;

public class TileEntitySound {
	private ISound sound;

	public TileEntitySound() { }

	public void playAlarm(double x, double y, double z, String name, float range) {
		Vector3d person = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		if (person != null) {
			double volume = 1.0F - Math.sqrt(person.distanceToSqr(x, y, z) / range / range);
			if (volume > 0) {
				if (volume < 0.3)
					volume = 0.3;
				sound = new SimpleSound(new SoundEvent(new ResourceLocation(name)), SoundCategory.MASTER, (float) volume, 1.0F, (float) person.x, (float) person.y, (float) person.z);
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
		return sound == null ? false : Minecraft.getInstance().getSoundManager().isActive(sound);
	}
}
