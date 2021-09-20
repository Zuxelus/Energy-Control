package com.zuxelus.energycontrol.crossmod.jei;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.GuiKitAssembler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class CrossJEI implements IModPlugin {
	private static final ResourceLocation id = new ResourceLocation(EnergyControl.MODID,"jei_plugin");

	@Override
	public ResourceLocation getPluginUid() {
		return id;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new IRecipeCategory[] { new KitAssemblerRecipeCategory(guiHelper) });
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		registry.addRecipeClickArea(GuiKitAssembler.class, 87, 35, 22, 15, new ResourceLocation[] { KitAssemblerRecipeCategory.id });
	}

	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		registry.addRecipes(KitAssemblerRecipeType.TYPE.getRecipes(Minecraft.getInstance().level), KitAssemblerRecipeCategory.id);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(ModItems.kit_assembler.get()), KitAssemblerRecipeCategory.id);
	}
}
