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
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

public class SoundHelper extends SimplePreparableReloadListener<Map<String, SoundEventRegistration>> implements IdentifiableResourceReloadListener {
	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "alarms");
	private static File alarms;
	private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Component.class, new Component.SerializerAdapter(RegistryAccess.EMPTY)).registerTypeAdapter(SoundEventRegistration.class, new SoundEventRegistrationSerializer()).create();
	private static final TypeToken<Map<String, SoundEventRegistration>> TYPE = new TypeToken<Map<String, SoundEventRegistration>>() {};

	public SoundHelper() {
		File configFolder = FabricLoader.getInstance().getConfigDir().toFile();
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

	// Added to the client resource packs by ReloadableResourceManagerImplMixin
	public static File getAlarmsFolder() {
		return alarms;
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	protected Map<String, SoundEventRegistration> prepare(ResourceManager manager, ProfilerFiller profiler) {
		EnergyControl.INSTANCE.availableAlarms = new ArrayList<String>();

		try {
			List<Resource> list = manager.getResourceStack(ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "sounds.json"));

			for (int i = list.size() - 1; i >= 0; --i) {
				Resource resource = list.get(i);
				Map<String, SoundEventRegistration> map = GsonHelper.fromJson(GSON, new InputStreamReader(resource.open(), StandardCharsets.UTF_8), TYPE);
				map.forEach((string, entry) -> EnergyControl.INSTANCE.availableAlarms.add(string.replace("alarm-", "")));
			}
		} catch (IOException ioexception) { 
			System.out.print(ioexception.getMessage());
		}
		return Maps.newHashMap();
	}

	@Override
	protected void apply(Map<String, SoundEventRegistration> loader, ResourceManager manager, ProfilerFiller profiler) {}
}