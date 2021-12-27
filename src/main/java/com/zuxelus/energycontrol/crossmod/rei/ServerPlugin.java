package com.zuxelus.energycontrol.crossmod.rei;

import com.zuxelus.energycontrol.containers.ContainerKitAssembler;

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleMenuInfoProvider;

public class ServerPlugin implements REIServerPlugin {

	@Override
	public void registerMenuInfo(MenuInfoRegistry registry) {
		//registry.register(KitAssemblerRecipeCategory.id, ContainerKitAssembler.class, SimpleMenuInfoProvider.of(RecipeBookGridMenuInfo::new));
		registry.register(KitAssemblerRecipeCategory.id, ContainerKitAssembler.class, SimpleMenuInfoProvider.of(KitAssemblerMenuInfo::new));
	}

	@Override
	public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
		registry.register(KitAssemblerRecipeCategory.id, KitAssemblerDisplay.serializer(KitAssemblerDisplay::new));
	}
}