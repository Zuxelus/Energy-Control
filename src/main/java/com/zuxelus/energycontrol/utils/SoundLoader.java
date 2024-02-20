package com.zuxelus.energycontrol.utils;

import java.io.File;
import java.util.function.Consumer;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.resource.PathPackResources;

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
		Pack pack = Pack.readMetaAndCreate(EnergyControl.MODID + "_alarms", Component.translatable("resourcePack.energycontrol"), false, (value) -> new PathPackResources(EnergyControl.NAME + " Alarms", true, alarms.toPath()), PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, PackSource.DEFAULT);
		if (pack != null)
			packs.accept(pack);
	}
}