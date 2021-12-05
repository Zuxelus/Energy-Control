package com.zuxelus.energycontrol.utils;

import java.io.File;
import java.util.function.Consumer;

import com.zuxelus.energycontrol.config.ConfigHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.Pack.PackConstructor;
import net.minecraftforge.event.AddPackFindersEvent;

public class SoundLoader implements RepositorySource {
	private static final SoundLoader INSTANCE = new SoundLoader();

	private SoundLoader() {}

	public static void locatePacks(AddPackFindersEvent event) {
		event.addRepositorySource(INSTANCE);
	}

	@Override
	public void loadPacks(Consumer<Pack> packs, PackConstructor factory) {
		if (!ConfigHandler.USE_CUSTOM_SOUNDS.get())
			return;
		File audioLoc = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath()  + File.separator + "alarms");
		Pack pack = Pack.create("name", false, () -> new FolderPackResources(audioLoc), factory,
				Pack.Position.BOTTOM, PackSource.DEFAULT);
		if (pack != null)
			packs.accept(pack);
	}
}