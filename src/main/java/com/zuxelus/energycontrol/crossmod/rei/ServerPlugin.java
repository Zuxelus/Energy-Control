package com.zuxelus.energycontrol.crossmod.rei;

import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;

// since 1.21.2 only the server knows the recipes: REI builds the displays there and sends them to the client
public class ServerPlugin implements REICommonPlugin {

	@Override
	public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
		registry.register(KitAssemblerDisplay.ID.getIdentifier(), KitAssemblerDisplay.SERIALIZER);
	}

	@Override
	public void registerDisplays(ServerDisplayRegistry registry) {
		registry.beginRecipeFiller(KitAssemblerRecipe.class)
			.filterType(KitAssemblerRecipeType.TYPE)
			.fill(entry -> new KitAssemblerDisplay(entry.value()));
	}
}
