package com.zuxelus.energycontrol.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class SoundHelper {
	private static final Gson gson = (new GsonBuilder()).registerTypeAdapter(SoundEventRegistration.class, new SoundEventRegistrationSerializer()).create();
	private static final ParameterizedType type = new ParameterizedType() {

		@Override
		public Type[] getActualTypeArguments() {
			return new Type[] { String.class, SoundEventRegistration.class };
		}

		@Override
		public Type getRawType() {
			return Map.class;
		}

		@Override
		public Type getOwnerType() {
			return null;
		}
	};

	public static void initSoundPack(File configFolder) {
		if (configFolder == null || !ConfigHandler.USE_CUSTOM_SOUNDS.get()) {
			importSound();
			return;
		}

		File audioLoc = new File(SoundLoader.alarms, "assets" + File.separator + EnergyControl.MODID + File.separator + "sounds");

		if (SoundLoader.alarms.exists())
			importSound();
		else {
			try {
				SoundLoader.alarms.mkdir();
				audioLoc.mkdirs();
				createSoundsJson();
				createPackMeta();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void createSoundsJson() throws IOException {
		JsonWriter writer = new JsonWriter(new FileWriter(SoundLoader.alarms.getAbsolutePath() + File.separator + "assets" + File.separator + EnergyControl.MODID + File.separator + "sounds.json"));
		writer.beginObject();
		writer.name("_comment").value("EXAMPLE 'alarm-name': {'category': 'master','sounds': [{'name': 'energycontrol:alarm-name','stream': true}]}");
		writer.endObject();
		writer.close();
	}

	private static void createPackMeta() throws IOException {
		JsonWriter writer = new JsonWriter(new FileWriter(SoundLoader.alarms.getAbsolutePath() + File.separator + "pack.mcmeta"));
		writer.beginObject();
		writer.name("pack");
		writer.beginObject();
		writer.name("description").value("Energy Control custom alarms");
		writer.name("pack_format").value(7); // for 1.17 only
		writer.endObject();
		writer.endObject();
		writer.close();
	}

	public static void importSound() {
		EnergyControl.INSTANCE.availableAlarms = new ArrayList<>();

		try {
			List<Resource> list = Minecraft.getInstance().getResourceManager().getResources(new ResourceLocation(EnergyControl.MODID, "sounds.json"));

			for (int i = list.size() - 1; i >= 0; --i) {
				Resource iresource = list.get(i);

				Map<String, SoundEventRegistration> map = gson.fromJson(new InputStreamReader(iresource.getInputStream()), type);
				map.forEach((str, soundList) -> EnergyControl.INSTANCE.availableAlarms.add(str.replace("alarm-", "")));
			}
		} catch (IOException ignored) {}
	}
}
