package com.zuxelus.energycontrol.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

public class SoundHelper extends SinglePreparationResourceReloadListener<Map<Identifier, Properties>> implements IdentifiableResourceReloadListener {
	private static File alarms;
	private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Text.class, new Text.Serializer()).registerTypeAdapter(SoundEntry.class, new SoundEntryDeserializer()).create();
	private static final ParameterizedType TYPE = new ParameterizedType() {
		@Override
		public Type[] getActualTypeArguments() {
			return new Type[] { String.class, SoundEntry.class };
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
				buildJSON();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void buildJSON() throws IOException {
		JsonWriter parse = new JsonWriter(new FileWriter(alarms.getAbsolutePath() + File.separator + "assets" + File.separator + EnergyControl.MODID + File.separator + "sounds.json"));
		parse.beginObject();
		parse.name("_comment").value("EXAMPLE 'alarm-name': {'category': 'master','sounds': [{'name': 'energycontrol:alarm-name','stream': true}]}");
		parse.endObject();
		parse.close();
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("energycontrol","custom_sounds");
	}

	@Override
	protected Map<Identifier, Properties> prepare(ResourceManager manager, Profiler profiler) {
		if (alarms != null) {
			DirectoryResourcePack pack = new DirectoryResourcePack(alarms);
			((ReloadableResourceManagerImpl) manager).addPack(pack);
		}
		EnergyControl.INSTANCE.availableAlarms = new ArrayList<String>();

		try {
			List<Resource> list = manager.getAllResources(new Identifier("energycontrol", "sounds.json"));

			for (int i = list.size() - 1; i >= 0; --i) {
				Resource resource = (Resource) list.get(i);

				try {
					Map map = (Map) JsonHelper.deserialize(GSON, (Reader) (new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)), (Type) TYPE);
					Iterator iterator = map.entrySet().iterator();

					while (iterator.hasNext()) {
						Entry<String, SoundEntry> entry = (Entry) iterator.next();
						EnergyControl.INSTANCE.availableAlarms.add(((String) entry.getKey()).replace("alarm-", ""));
					}
				} catch (RuntimeException runtimeexception) { }
			}
		} catch (IOException ioexception) { 
			System.out.print(ioexception.getMessage());
		}
		return Maps.newHashMap();
	}

	@Override
	protected void apply(Map<Identifier, Properties> loader, ResourceManager manager, Profiler profiler) { }
}
