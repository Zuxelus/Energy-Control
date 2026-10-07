package com.zuxelus.energycontrol.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

public class SoundHelper extends SimplePreparableReloadListener<Void> {
	public static final Identifier ID = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "alarms");
	private static File alarms;

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
	protected Void prepare(ResourceManager manager, ProfilerFiller profiler) {
		EnergyControl.INSTANCE.availableAlarms = new ArrayList<String>();

		try {
			List<Resource> list = manager.getResourceStack(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "sounds.json"));

			for (int i = list.size() - 1; i >= 0; --i) {
				Resource resource = list.get(i);
				// only the sound event names are needed here, the sounds themselves are loaded by the SoundManager
				JsonObject json = GsonHelper.parse(new InputStreamReader(resource.open(), StandardCharsets.UTF_8));
				for (String name : json.keySet())
					if (!name.startsWith("_"))
						EnergyControl.INSTANCE.availableAlarms.add(name.replace("alarm-", ""));
			}
		} catch (IOException ioexception) { 
			System.out.print(ioexception.getMessage());
		}
		return null;
	}

	@Override
	protected void apply(Void loader, ResourceManager manager, ProfilerFiller profiler) {}
}