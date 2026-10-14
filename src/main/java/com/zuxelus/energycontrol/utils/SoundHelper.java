package com.zuxelus.energycontrol.utils;

import java.io.File;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackMetadataResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.event.AddPackFindersEvent;

public class SoundHelper implements RepositorySource {
	private static final SoundHelper INSTANCE = new SoundHelper();
	private static File alarms;

	private SoundHelper() {}

	// available alarms are read by the client reload listener (see ClientProxy), here only the custom pack folder is created
	public static void initSoundPack(File gameFolder) {
		if (gameFolder == null || !ConfigHandler.USE_CUSTOM_SOUNDS.get())
			return;

		// the sound files alarm-NAME.ogg are put into this folder, see AlarmFolderPack
		if (alarms == null)
			alarms = new File(gameFolder, "alarms");
		if (!alarms.exists())
			alarms.mkdirs();
	}

	public static void locatePacks(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.CLIENT_RESOURCES)
			event.addRepositorySource(INSTANCE);
	}

	@Override
	public void loadPacks(Consumer<Pack> packs) { // client
		alarms = new File(Minecraft.getInstance().gameDirectory, "alarms");
		if (!alarms.exists())
			return;
		Path folder = alarms.toPath();
		PackLocationInfo location = new PackLocationInfo(EnergyControl.MODID + "_alarms", Component.translatable("resourcePack.energycontrol"), PackSource.DEFAULT, Optional.empty());
		Pack.ResourcesSupplier resources = new Pack.ResourcesSupplier() {
			@Override
			public PackMetadataResources openMetadata(PackLocationInfo info) {
				return new AlarmFolderPack(info, folder);
			}

			@Override
			public Stream<PackResources> openResources(PackLocationInfo info, Pack.Metadata metadata) {
				return Stream.of(new AlarmFolderPack(info, folder));
			}
		};
		// required: the pack is always enabled, there is nothing to switch on in the resource pack screen
		Pack pack = Pack.readMetaAndCreate(location, resources, PackType.CLIENT_RESOURCES, new PackSelectionConfig(true, Pack.Position.BOTTOM, false));
		if (pack != null)
			packs.accept(pack);
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
