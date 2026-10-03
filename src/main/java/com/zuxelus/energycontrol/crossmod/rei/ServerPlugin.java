package com.zuxelus.energycontrol.crossmod.rei;

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;

public class ServerPlugin implements REIServerPlugin {

	@Override
	public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
		registry.register(KitAssemblerRecipeCategory.id, KitAssemblerDisplay.serializer(KitAssemblerDisplay::new));
	}
}