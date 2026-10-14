package com.zuxelus.energycontrol.utils;

import java.io.Reader;
import com.google.gson.JsonParser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class SoundHelper extends SimplePreparableReloadListener<Void> {
	public static final Identifier ID = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "alarms");
	private static File alarms;

	public SoundHelper() {
		initSoundPack(FabricLoader.getInstance().getGameDir().toFile());
	}

	// available alarms are read by the client reload listener, here only the custom pack folder is created
	public static void initSoundPack(File gameFolder) {
		if (gameFolder == null || !ConfigHandler.useCustomSounds)
			return;

		// the sound files alarm-NAME.ogg are put into this folder, see AlarmFolderPack
		if (alarms == null)
			alarms = new File(gameFolder, "alarms");
		if (!alarms.exists())
			alarms.mkdirs();
	}

	// Added to the client resource packs by ReloadableResourceManagerImplMixin
	public static File getAlarmsFolder() {
		return alarms;
	}

	@Override
	protected Void prepare(ResourceManager manager, ProfilerFiller profiler) {
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
		return null;
	}

	@Override
	protected void apply(Void loader, ResourceManager manager, ProfilerFiller profiler) {}
}