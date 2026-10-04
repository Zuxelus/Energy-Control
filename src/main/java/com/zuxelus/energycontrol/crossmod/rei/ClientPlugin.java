package com.zuxelus.energycontrol.crossmod.rei;

import com.zuxelus.energycontrol.gui.GuiKitAssembler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;

@Environment(EnvType.CLIENT)
public final class ClientPlugin implements REIClientPlugin {

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.add(new KitAssemblerRecipeCategory());
		// what the deprecated removePlusButton() did: a null area hides the "+" button
		registry.configure(KitAssemblerRecipeCategory.id, config -> config.setPlusButtonArea(bounds -> null));
		registry.addWorkstations(KitAssemblerRecipeCategory.id, EntryStacks.of(new ItemStack(ModItems.kit_assembler)));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		registry.registerRecipeFiller(KitAssemblerRecipe.class, KitAssemblerRecipeType.TYPE, (RecipeEntry<KitAssemblerRecipe> entry) -> new KitAssemblerDisplay(entry.value()));
	}

	@Override
	public void registerScreens(ScreenRegistry registry) {
		registry.registerContainerClickArea(new Rectangle(87, 35, 22, 15), GuiKitAssembler.class, new CategoryIdentifier[] { KitAssemblerRecipeCategory.id });
	}

	// REI's SimpleTransferHandler moves only one item per slot, recipes here need several
	@Override
	public void registerTransferHandlers(TransferHandlerRegistry registry) {
		registry.register(new KitAssemblerTransferHandler());
	}
}