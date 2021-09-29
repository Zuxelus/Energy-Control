package com.zuxelus.energycontrol.crossmod.jei;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.GuiKitAssembler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
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
		registerItem(registry, ModItems.white_lamp.get(), "ec.jei.blockLightWhite");
		registerItem(registry, ModItems.orange_lamp.get(), "ec.jei.blockLightOrange");
		registerItem(registry, ModItems.howler_alarm.get(), "ec.jei.blockHowlerAlarm");
		registerItem(registry, ModItems.industrial_alarm.get(), "ec.jei.blockIndustrialAlarm");
		registerItem(registry, ModItems.thermal_monitor.get(), "ec.jei.blockThermalMonitor");
		registerItem(registry, ModItems.info_panel.get(), "ec.jei.blockInfoPanel");
		registerItem(registry, ModItems.info_panel_extender.get(), "ec.jei.blockInfoPanelExtender");
		registerItem(registry, ModItems.info_panel_advanced.get(), "ec.jei.blockInfoPanelAdvanced");
		registerItem(registry, ModItems.info_panel_advanced_extender.get(), "ec.jei.blockInfoPanelAdvancedExtender");
		registerItem(registry, ModItems.holo_panel.get(), "ec.jei.blockHoloPanel");
		registerItem(registry, ModItems.kit_assembler.get(), "ec.jei.blockKitAssembler");

		registerItem(registry, ModItems.upgrade_color.get(), "ec.jei.upgradeColor");
		registerItem(registry, ModItems.upgrade_range.get(), "ec.jei.upgradeRange");
		registerItem(registry, ModItems.upgrade_touch.get(), "ec.jei.upgradeTouch");

		registerItem(registry, ModItems.kit_app_eng, "ec.jei.kitAppEng");
		registerItem(registry, ModItems.kit_big_reactors, "ec.jei.kitBigReactors");
		registerItem(registry, ModItems.kit_energy.get(), "ec.jei.kitEnergy");
		registerItem(registry, ModItems.kit_liquid.get(), "ec.jei.kitLiquid");
		registerItem(registry, ModItems.kit_liquid_advanced.get(), "ec.jei.kitLiquidAdv");
		registerItem(registry, ModItems.kit_toggle.get(), "ec.jei.kitToggle");
		
		registerItem(registry, ModItems.card_energy.get(), "ec.jei.cards");
		registerItem(registry, ModItems.card_inventory.get(), "ec.jei.cards");
		registerItem(registry, ModItems.card_liquid_advanced.get(), "ec.jei.cards");
		registerItem(registry, ModItems.card_liquid.get(), "ec.jei.cards");
		registerItem(registry, ModItems.card_redstone.get(), "ec.jei.cards");
		registerItem(registry, ModItems.card_toggle.get(), "ec.jei.cards");
	}

	@SuppressWarnings("deprecation")
	private void registerItem(IRecipeRegistration registry, IItemProvider item, String name) {
		if (item != null)
			registry.addIngredientInfo(new ItemStack(item), VanillaTypes.ITEM, I18n.get(name));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(ModItems.kit_assembler.get()), KitAssemblerRecipeCategory.id);
	}
}
