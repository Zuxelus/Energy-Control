package com.zuxelus.energycontrol.crossmod.jei;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.GuiKitAssembler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

@Environment(EnvType.CLIENT)
public class CrossJEI implements IModPlugin {
	private static final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "jei_plugin");

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
		registry.addRecipeClickArea(GuiKitAssembler.class, 87, 35, 22, 15, KitAssemblerRecipeCategory.recipeType);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registry) {
		registry.addRecipeTransferHandler(new KitAssemblerTransferHandler(registry.getTransferHelper()), KitAssemblerRecipeCategory.recipeType);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		registry.addRecipes(KitAssemblerRecipeCategory.recipeType, KitAssemblerRecipeType.TYPE.getRecipes(Minecraft.getInstance().level));
		registerItem(registry, ModItems.white_lamp, "ec.jei.blockLightWhite");
		registerItem(registry, ModItems.orange_lamp, "ec.jei.blockLightOrange");
		registerItem(registry, ModItems.howler_alarm, "ec.jei.blockHowlerAlarm");
		registerItem(registry, ModItems.industrial_alarm, "ec.jei.blockIndustrialAlarm");
		registerItem(registry, ModItems.thermal_monitor, "ec.jei.blockThermalMonitor");
		registerItem(registry, ModItems.info_panel, "ec.jei.blockInfoPanel");
		registerItem(registry, ModItems.info_panel_extender, "ec.jei.blockInfoPanelExtender");
		registerItem(registry, ModItems.info_panel_advanced, "ec.jei.blockInfoPanelAdvanced");
		registerItem(registry, ModItems.info_panel_advanced_extender, "ec.jei.blockInfoPanelAdvancedExtender");
		registerItem(registry, ModItems.holo_panel, "ec.jei.blockHoloPanel");
		registerItem(registry, ModItems.kit_assembler, "ec.jei.blockKitAssembler");

		registerItem(registry, ModItems.upgrade_color, "ec.jei.upgradeColor");
		registerItem(registry, ModItems.upgrade_range, "ec.jei.upgradeRange");
		registerItem(registry, ModItems.upgrade_touch, "ec.jei.upgradeTouch");
		registerItem(registry, ModItems.upgrade_web, "ec.jei.upgradeWeb");

		registerItem(registry, ModItems.kit_app_eng, "ec.jei.kitAppEng");
		registerItem(registry, ModItems.kit_big_reactors, "ec.jei.kitBigReactors");
		registerItem(registry, ModItems.kit_energy, "ec.jei.kitEnergy");
		registerItem(registry, ModItems.kit_liquid, "ec.jei.kitLiquid");
		registerItem(registry, ModItems.kit_liquid_advanced, "ec.jei.kitLiquidAdv");
		registerItem(registry, ModItems.kit_toggle, "ec.jei.kitToggle");
		
		registerItem(registry, ModItems.card_energy, "ec.jei.cards");
		registerItem(registry, ModItems.card_inventory, "ec.jei.cards");
		registerItem(registry, ModItems.card_liquid_advanced, "ec.jei.cards");
		registerItem(registry, ModItems.card_liquid, "ec.jei.cards");
		registerItem(registry, ModItems.card_redstone, "ec.jei.cards");
		registerItem(registry, ModItems.card_toggle, "ec.jei.cards");

		registerItem(registry, ModItems.card_holder, "ec.jei.itemCardHolder");
		registerItem(registry, ModItems.portable_panel, "ec.jei.itemPortablePanel");
	}

	private void registerItem(IRecipeRegistration registry, ItemLike item, String name) {
		if (item != null)
			registry.addIngredientInfo(new ItemStack(item), VanillaTypes.ITEM_STACK, Component.literal(I18n.get(name)));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(ModItems.kit_assembler), KitAssemblerRecipeCategory.recipeType);
	}
}
