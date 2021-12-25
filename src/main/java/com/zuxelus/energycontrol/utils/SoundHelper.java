package com.zuxelus.energycontrol.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundEntryDeserializer;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

public class SoundHelper extends SinglePreparationResourceReloader<Map<String, SoundEntry>> implements IdentifiableResourceReloadListener {
	public static final Identifier ID = new Identifier(EnergyControl.MODID, "alarms");
	private static File alarms;
	private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Text.class, new Text.Serializer()).registerTypeAdapter(SoundEntry.class, new SoundEntryDeserializer()).create();
	private static final TypeToken<Map<String, SoundEntry>> TYPE = new TypeToken<Map<String, SoundEntry>>() {};

	public SoundHelper() {
		File configFolder = FabricLoader.getInstance().getConfigDirectory();
		if (configFolder == null || !ConfigHandler.useCustomSounds)
			return;

		alarms = new File(configFolder, "alarms");
		File audioLoc = new File(alarms, "assets" + File.separator + EnergyControl.MODID + File.separator + "sounds");

		if (!alarms.exists()) {
			try {
				alarms.mkdir();
				audioLoc.mkdirs();
				createSoundsJson();
				createPackMeta();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void createSoundsJson() throws IOException {
		JsonWriter writer = new JsonWriter(new FileWriter(alarms.getAbsolutePath() + File.separator + "assets" + File.separator + EnergyControl.MODID + File.separator + "sounds.json"));
		writer.beginObject();
		writer.name("_comment").value("EXAMPLE 'alarm-name': {'category': 'master','sounds': [{'name': 'energycontrol:alarm-name','stream': true}]}");
		writer.endObject();
		writer.close();
	}

	private static void createPackMeta() throws IOException {
		JsonWriter writer = new JsonWriter(new FileWriter(SoundHelper.alarms.getAbsolutePath() + File.separator + "pack.mcmeta"));
		writer.beginObject();
		writer.name("pack");
		writer.beginObject();
		writer.name("description").value("Energy Control custom alarms");
		writer.name("pack_format").value(8); // for 1.18
		writer.endObject();
		writer.endObject();
		writer.close();
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	@Override
	protected Map<String, SoundEntry> prepare(ResourceManager manager, Profiler profiler) {
		if (alarms != null) {
			DirectoryResourcePack pack = new DirectoryResourcePack(alarms);
			((ReloadableResourceManagerImpl) manager).addPack(pack);
		}
		EnergyControl.INSTANCE.availableAlarms = new ArrayList<String>();

		try {
			List<Resource> list = manager.getAllResources(new Identifier(EnergyControl.MODID, "sounds.json"));

			for (int i = list.size() - 1; i >= 0; --i) {
				Resource resource = list.get(i);
				Map<String, SoundEntry> map = JsonHelper.deserialize(GSON, new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8), TYPE);
				map.forEach((string, entry) -> EnergyControl.INSTANCE.availableAlarms.add(string.replace("alarm-", "")));
			}
		} catch (IOException ioexception) { 
			System.out.print(ioexception.getMessage());
		}
		return Maps.newHashMap();
	}

	@Override
	protected void apply(Map<String, SoundEntry> loader, ResourceManager manager, Profiler profiler) {}
}