package com.zuxelus.energycontrol.crossmod.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.zuxelus.energycontrol.blocks.BlockLight;
import com.zuxelus.energycontrol.gui.GuiKitAssembler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class CrossJEI implements IModPlugin {
	public static IJeiRuntime jeiRuntime = null;

	@Override
	public void register(IModRegistry registry) {
		registry.addRecipeCategories(new KitAssemblerRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeHandlers(new KitAssemblerRecipeHandler()); // In 1.12 handleRecipes
		registry.addRecipeCategoryCraftingItem(new ItemStack(ModItems.blockKitAssembler), KitAssemblerRecipeCategory.id); // In 1.12 addRecipeCatalyst

		List<KitAssemblerRecipeWrapper> recipes = new ArrayList<KitAssemblerRecipeWrapper>();
		for (KitAssemblerRecipe recipe : KitAssemblerRecipe.getRecipes())
			recipes.add(new KitAssemblerRecipeWrapper(recipe));
		registry.addRecipes(recipes);

		registry.addRecipeClickArea(GuiKitAssembler.class, 87, 35, 22, 15, new String[] { KitAssemblerRecipeCategory.id });

		registerBlock(registry, ModItems.blockLight,"ec.jei.blockLightWhite");
		registry.addDescription(new ItemStack(ModItems.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF), I18n.format("ec.jei.blockLightOrange"));
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
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_DRACONIC, "ec.jei.kitDraconic");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_ENERGY, "ec.jei.kitEnergy");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_GALACTICRAFT, "ec.jei.kitGalacticraft");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_GENERATOR, "ec.jei.kitGenerator");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_LIQUID, "ec.jei.kitLiquid");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_LIQUID_ADVANCED, "ec.jei.kitLiquidAdv");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_REACTOR, "ec.jei.kitReactor");
		registerItem(registry, ModItems.itemKit, ItemCardType.KIT_TOGGLE, "ec.jei.kitToggle");
		for(Integer key : ItemCardMain.getCardIds())
			if (key != ItemCardType.CARD_TEXT && key != ItemCardType.CARD_TIME)
				registerItem(registry, ModItems.itemCard, key, "ec.jei.cards");
	}

	private void registerBlock(@Nonnull IModRegistry registry, Block block, String name) {
		if (block != null)
			registry.addDescription(new ItemStack(block), I18n.format(name));
	}

	private void registerItem(@Nonnull IModRegistry registry, Item item, int meta, String name) {
		if (item != null)
			registry.addDescription(new ItemStack(item, 1, meta), I18n.format(name));
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
		jeiRuntime = iJeiRuntime;
	}

	@Override
	public void registerIngredients(IModIngredientRegistration arg0) {}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry arg0) {}
}
