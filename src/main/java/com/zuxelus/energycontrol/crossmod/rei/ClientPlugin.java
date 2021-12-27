package com.zuxelus.energycontrol.crossmod.rei;

import com.zuxelus.energycontrol.gui.GuiKitAssembler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public final class ClientPlugin implements REIClientPlugin {

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.add(new KitAssemblerRecipeCategory());
		registry.removePlusButton(KitAssemblerRecipeCategory.id);
		registry.addWorkstations(KitAssemblerRecipeCategory.id, EntryStacks.of(new ItemStack(ModItems.kit_assembler)));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		registry.registerFiller(KitAssemblerRecipe.class, KitAssemblerDisplay::new);
	}

	@Override
	public void registerScreens(ScreenRegistry registry) {
		registry.registerContainerClickArea(new Rectangle(87, 35, 22, 15), GuiKitAssembler.class, new CategoryIdentifier[] { KitAssemblerRecipeCategory.id });
	}

	/*public void registerTransferHandlers(TransferHandlerRegistry registry) {
		registry.register(new KitAssemblerTransferHandler());
	}*/
}