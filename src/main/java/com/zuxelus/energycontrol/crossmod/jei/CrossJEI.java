package com.zuxelus.energycontrol.crossmod.jei;

import com.zuxelus.energycontrol.blocks.BlockLight;
import com.zuxelus.energycontrol.containers.ContainerKitAssembler;
import com.zuxelus.energycontrol.gui.GuiKitAssembler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@JEIPlugin
public class CrossJEI implements IModPlugin {

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new KitAssemblerRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void register(IModRegistry registry) {
		registry.addRecipes(KitAssemblerRecipe.getRecipes(), KitAssemblerRecipeCategory.id);
		registry.handleRecipes(KitAssemblerRecipe.class, recipe -> new KitAssemblerRecipeWrapper(recipe), KitAssemblerRecipeCategory.id);
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerKitAssembler.class, KitAssemblerRecipeCategory.id, 1, 3, 6, 36);
		registry.addRecipeCatalyst(new ItemStack(ModItems.blockKitAssembler), KitAssemblerRecipeCategory.id);
		registry.addRecipeClickArea(GuiKitAssembler.class, 87, 35, 22, 15, new String[] { KitAssemblerRecipeCategory.id });

		registerBlock(registry, ModItems.blockLight,"ec.jei.blockLightWhite");
		registry.addIngredientInfo(new ItemStack(ModItems.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF), VanillaTypes.ITEM, I18n.format("ec.jei.blockLightOrange"));
		registerBlock(registry, ModItems.blockHowlerAlarm,"ec.jei.blockHowlerAlarm");
		registerBlock(registry, ModItems.blockIndustrialAlarm,"ec.jei.blockIndustrialAlarm");
		registerBlock(registry, ModItems.blockThermalMonitor,"ec.jei.blockThermalMonitor");
		registerBlock(registry, ModItems.blockInfoPanel,"ec.jei.blockInfoPanel");
		registerBlock(registry, ModItems.blockInfoPanelExtender,"ec.jei.blockInfoPanelExtender");
		registerBlock(registry, ModItems.blockInfoPanelAdvanced,"ec.jei.blockInfoPanelAdvanced");
		registerBlock(registry, ModItems.blockInfoPanelAdvancedExtender,"ec.jei.blockInfoPanelAdvancedExtender");
		registerBlock(registry, ModItems.blockHoloPanel,"ec.jei.blockHoloPanel");
		registerBlock(registry, ModItems.blockRangeTrigger,"ec.jei.blockRangeTrigger");
		registerBlock(registry, ModItems.blockRemoteThermo,"ec.jei.blockRemoteThermo");
		registerBlock(registry, ModItems.blockAverageCounter,"ec.jei.blockAverageCounter");
		registerBlock(registry, ModItems.blockEnergyCounter,"ec.jei.blockEnergyCounter");
		registerBlock(registry, ModItems.blockKitAssembler,"ec.jei.blockKitAssembler");
		registerBlock(registry, ModItems.blockAfsu,"ec.jei.blockAFSU");

		registerItem(registry, ModItems.itemAFB, 0, "ec.jei.itemAFB");
		registerItem(registry, ModItems.itemCardHolder, 0, "ec.jei.itemCardHolder");
		registerItem(registry, ModItems.itemPortablePanel, 0, "ec.jei.itemPortablePanel");

		registerItem(registry, ModItems.itemUpgrade, ItemUpgrade.DAMAGE_COLOR, "ec.jei.upgradeColor");
		registerItem(registry, ModItems.itemUpgrade, ItemUpgrade.DAMAGE_RANGE, "ec.jei.upgradeRange");
		registerItem(registry, ModItems.itemUpgrade, ItemUpgrade.DAMAGE_TOUCH, "ec.jei.upgradeTouch");

		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_APPENG, "ec.jei.kitAppEng");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_BIG_REACTORS, "ec.jei.kitBigReactors");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_COUNTER, "ec.jei.kitCounter");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_DRACONIC_EVOLUTION, "ec.jei.kitDraconic");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_ENERGY, "ec.jei.kitEnergy");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_GALACTICRAFT, "ec.jei.kitGalacticraft");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_LIQUID, "ec.jei.kitLiquid");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_LIQUID_ADVANCED, "ec.jei.kitLiquidAdv");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_TOGGLE, "ec.jei.kitToggle");
		for(Integer key : ItemCardMain.getCardIds())
			if (key != ItemCardType.CARD_TEXT && key != ItemCardType.CARD_TIME)
				registerItem(registry, ModItems.itemCard, key, "ec.jei.cards");
	}

	private void registerBlock(@Nonnull IModRegistry registry, Block block, String name) {
		if (block != null)
			registry.addIngredientInfo(new ItemStack(block), VanillaTypes.ITEM, I18n.format(name));
	}

	private void registerItem(@Nonnull IModRegistry registry, Item item, int meta, String name) {
		if (item != null)
			registry.addIngredientInfo(new ItemStack(item, 1, meta), VanillaTypes.ITEM, I18n.format(name));
	}
}
