package com.zuxelus.energycontrol.tileentities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class TileEntitySound {
	private ISound sound;

	public TileEntitySound() { }

	public void playAlarm(double x, double y, double z, String name, float range) {
		@SuppressWarnings("resource")
		Vec3d person = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
		if (person != null) {
			double volume = 1.0F - Math.sqrt(person.squareDistanceTo(x, y, z) / range / range);
			if (volume > 0) {
				ISound sound = new SimpleSound(new SoundEvent(new ResourceLocation(name)), SoundCategory.MASTER, (float) volume, 1.0F, (float) x, (float) y, (float) z);
				Minecraft.getInstance().getSoundHandler().play(sound);
				return;
			}
		}
		sound = null;
	}

	public void stopAlarm() {
		if (sound != null) {
			Minecraft.getInstance().getSoundHandler().stop(sound);
			sound = null;
		}
	}

	public boolean isPlaying() {
		return sound == null ? false : Minecraft.getInstance().getSoundHandler().isPlaying(sound);
	}
}
