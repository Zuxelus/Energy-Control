package com.zuxelus.energycontrol.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.loader.api.FabricLoader;

public class ModMenu implements ModMenuApi {

	// Cloth Config is optional: without it there is no config screen and ModMenu hides the Configure button
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> FabricLoader.getInstance().isModLoaded("cloth-config") ? ClothConfigScreen.create(parent) : null;
	}
}
