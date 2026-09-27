package com.zuxelus.energycontrol.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class SoundHelper {
	// available alarms are read by the client reload listener (see ClientProxy), here only the custom pack folder is created
	public static void initSoundPack(File configFolder) {
		if (configFolder == null || !ConfigHandler.USE_CUSTOM_SOUNDS.get())
			return;

		if (SoundLoader.alarms == null)
			SoundLoader.alarms = new File(configFolder, "alarms");
		File audioLoc = new File(SoundLoader.alarms, "assets" + File.separator + EnergyControl.MODID + File.separator + "sounds");

		if (!SoundLoader.alarms.exists()) {
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
		writer.name("min_format").value(97); // for 26.3
		writer.name("max_format").value(97);
		writer.endObject();
		writer.endObject();
		writer.close();
	}

	public static void importSound() {
		importSound(Minecraft.getInstance().getResourceManager());
	}

	// called on every client resource reload, so alarms from resource packs are picked up
	public static void importSound(ResourceManager manager) {
		List<String> alarms = new ArrayList<>();

		List<Resource> list = manager.getResourceStack(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "sounds.json"));
		for (int i = list.size() - 1; i >= 0; --i) {
			try (Reader reader = list.get(i).openAsReader()) {
				JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
				for (String name : json.keySet())
					if (name.startsWith("alarm-"))
						alarms.add(name.replace("alarm-", ""));
			} catch (Exception ignored) {}
		}
		EnergyControl.INSTANCE.availableAlarms = alarms;
	}
}
