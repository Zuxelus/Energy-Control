package com.zuxelus.energycontrol.utils;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.neoforged.neoforge.event.AddPackFindersEvent;

public class SoundLoader implements RepositorySource {
	private static final SoundLoader INSTANCE = new SoundLoader();
	public static File alarms;

	private SoundLoader() {}

	public static void locatePacks(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.CLIENT_RESOURCES)
			event.addRepositorySource(INSTANCE);
	}

	@Override
	public void loadPacks(Consumer<Pack> packs) { // client
		alarms = new File(Minecraft.getInstance().gameDirectory, "alarms");
		if (!alarms.exists())
			return;
		PackLocationInfo location = new PackLocationInfo(EnergyControl.MODID + "_alarms", Component.translatable("resourcePack.energycontrol"), PackSource.DEFAULT, Optional.empty());
		Pack pack = Pack.readMetaAndCreate(location, new PathPackResources.PathResourcesSupplier(alarms.toPath()), PackType.CLIENT_RESOURCES, new PackSelectionConfig(false, Pack.Position.BOTTOM, false));
		if (pack != null)
			packs.accept(pack);
	}
}
