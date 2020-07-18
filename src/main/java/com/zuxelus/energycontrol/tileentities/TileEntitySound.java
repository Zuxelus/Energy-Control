package com.zuxelus.energycontrol.tileentities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.client.FMLClientHandler;

public class TileEntitySound {
	private static final float DEFAULT_RANGE = 16F;
	private PositionedSoundRecord sound;

	public TileEntitySound() { }

	public void stopAlarm() {
		if (sound != null) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
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
		return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(sound);
	}

	public PositionedSoundRecord playAlarm(double x, double y, double z, String name, float volume) {
		float range = DEFAULT_RANGE;

		if (volume > 1.0F)
			range *= volume;

		Entity person = FMLClientHandler.instance().getClient().getRenderViewEntity();

		if (person != null && volume > 0 && person.getDistanceSq(x, y, z) < range * range) {
			PositionedSoundRecord sound = new PositionedSoundRecord(new SoundEvent(new ResourceLocation(name)), SoundCategory.MASTER, volume, 1.0F, (float) x, (float) y, (float) z);
			Minecraft.getMinecraft().getSoundHandler().playSound(sound);
			return sound;
		}
		return null;
	}
}
