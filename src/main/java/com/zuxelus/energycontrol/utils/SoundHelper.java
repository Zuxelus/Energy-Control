package com.zuxelus.energycontrol.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundListSerializer;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;

public class SoundHelper {
	private static final Gson gson = (new GsonBuilder()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
	private static File alarms;
	
	private static final ParameterizedType type = new ParameterizedType() {
		private static final String __OBFID = "CL_00001148";

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
		if (configFolder == null || !EnergyControl.config.useCustomSounds)
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
		EnergyControl.instance.availableAlarms = new ArrayList<String>();

		try {
			List list = Minecraft.getMinecraft().getResourceManager().getAllResources(new ResourceLocation("energycontrol", "sounds.json"));

			for (int i = list.size() - 1; i >= 0; --i) {
				IResource iresource = (IResource) list.get(i);

				try {
					Map map = (Map) gson.fromJson(new InputStreamReader(iresource.getInputStream()), type);
					Iterator iterator1 = map.entrySet().iterator();

					while (iterator1.hasNext()) {
						Entry entry = (Entry) iterator1.next();
						EnergyControl.instance.availableAlarms.add(((String) entry.getKey()).replace("alarm-", ""));
					}
				} catch (RuntimeException runtimeexception) { }
			}
		} catch (IOException ioexception) {}
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
				FolderResourcePack pack = new FolderResourcePack(alarms);
				((SimpleReloadableResourceManager) resourceManager).reloadResourcePack(pack);
			}
		}
	}
}
