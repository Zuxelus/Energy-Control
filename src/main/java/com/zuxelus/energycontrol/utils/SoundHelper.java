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
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundListSerializer;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

public class SoundHelper {
	private static final Gson gson = (new GsonBuilder()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
	private static File alarms;

	private static final ParameterizedType type = new ParameterizedType() {

		@Override
		public Type[] getActualTypeArguments() {
			return new Type[] { String.class, SoundList.class };
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

	public static void initSound(File configFolder) {
		if (configFolder == null || !ConfigHandler.USE_CUSTOM_SOUNDS.get())
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

		public static void importSound() {
		EnergyControl.INSTANCE.availableAlarms = new ArrayList<>();

		try {
			List<IResource> list = Minecraft.getInstance().getResourceManager().getAllResources(new ResourceLocation(EnergyControl.MODID, "sounds.json"));

			for (int i = list.size() - 1; i >= 0; --i) {
				IResource iresource = list.get(i);

				Map<String, SoundList> map = gson.fromJson(new InputStreamReader(iresource.getInputStream()), type);
				map.forEach((str, soundList) -> EnergyControl.INSTANCE.availableAlarms.add(str.replace("alarm-", "")));
			}
		} catch (IOException ignored) {}
	}

	private static void buildJSON() throws IOException {
		JsonWriter parse = new JsonWriter(new FileWriter(alarms.getAbsolutePath() + File.separator + "assets" + File.separator + EnergyControl.MODID + File.separator + "sounds.json"));
		parse.beginObject();
		parse.name("_comment").value("EXAMPLE 'alarm-name': {'category': 'master','sounds': [{'name': 'energycontrol:alarm-name','stream': true}]}");
		parse.endObject();
		parse.close();
	}

	public static class SoundLoader implements ISelectiveResourceReloadListener {

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
			if (resourcePredicate.test(VanillaResourceType.SOUNDS) && resourceManager instanceof SimpleReloadableResourceManager && alarms != null) {
				FolderPack pack = new FolderPack(alarms);
				((SimpleReloadableResourceManager) resourceManager).addResourcePack(pack);
			}
		}
	}
}
