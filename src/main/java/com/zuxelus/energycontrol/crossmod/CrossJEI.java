package com.zuxelus.energycontrol.crossmod;

import javax.annotation.Nonnull;

import com.zuxelus.energycontrol.blocks.BlockLight;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class CrossJEI implements IModPlugin {

	@Override
	public void register(@Nonnull IModRegistry registry) {
		registerBlock(registry, ItemHelper.blockLight,"ec.jei.blockLightWhite");
		registry.addIngredientInfo(new ItemStack(ItemHelper.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF), VanillaTypes.ITEM, I18n.format("ec.jei.blockLightOrange"));
		registerBlock(registry, ItemHelper.howlerAlarm,"ec.jei.blockHowlerAlarm");
		registerBlock(registry, ItemHelper.industrialAlarm,"ec.jei.blockIndustrialAlarm");
		registerBlock(registry, ItemHelper.thermalMonitor,"ec.jei.blockThermalMonitor");
		registerBlock(registry, ItemHelper.infoPanel,"ec.jei.blockInfoPanel");
		registerBlock(registry, ItemHelper.infoPanelExtender,"ec.jei.blockInfoPanelExtender");
		registerBlock(registry, ItemHelper.infoPanelAdvanced,"ec.jei.blockInfoPanelAdvanced");
		registerBlock(registry, ItemHelper.infoPanelAdvancedExtender,"ec.jei.blockInfoPanelAdvancedExtender");
		registerBlock(registry, ItemHelper.rangeTrigger,"ec.jei.blockRangeTrigger");
		registerBlock(registry, ItemHelper.remoteThermo,"ec.jei.blockRemoteThermo");
		registerBlock(registry, ItemHelper.averageCounter,"ec.jei.blockAverageCounter");
		registerBlock(registry, ItemHelper.energyCounter,"ec.jei.blockEnergyCounter");
		registerBlock(registry, ItemHelper.kitAssembler,"ec.jei.blockKitAssembler");
		registerBlock(registry, ItemHelper.afsu,"ec.jei.blockAFSU");

		registerItem(registry, ItemHelper.itemAFB, 0, "ec.jei.itemAFB");
		registerItem(registry, ItemHelper.itemCardHolder, 0, "ec.jei.itemCardHolder");
		registerItem(registry, ItemHelper.itemPortablePanel, 0, "ec.jei.itemPortablePanel");
		
		registerItem(registry, ItemHelper.itemUpgrade, ItemUpgrade.DAMAGE_COLOR, "ec.jei.upgradeColor");
		registerItem(registry, ItemHelper.itemUpgrade, ItemUpgrade.DAMAGE_RANGE, "ec.jei.upgradeRange");
		registerItem(registry, ItemHelper.itemUpgrade, ItemUpgrade.DAMAGE_TOUCH, "ec.jei.upgradeTouch");
		
		registerItem(registry, ItemHelper.itemKit, ItemCardType.KIT_APPENG, "ec.jei.kitAppEng");
		registerItem(registry, ItemHelper.itemKit, ItemCardType.KIT_BIG_REACTORS, "ec.jei.kitBigReactors");
		registerItem(registry, ItemHelper.itemKit, ItemCardType.KIT_COUNTER, "ec.jei.kitCounter");
		registerItem(registry, ItemHelper.itemKit, ItemCardType.KIT_DRACONIC, "ec.jei.kitDraconic");
		registerItem(registry, ItemHelper.itemKit, ItemCardType.KIT_ENERGY, "ec.jei.kitEnergy");
		registerItem(registry, ItemHelper.itemKit, ItemCardType.KIT_GALACTICRAFT, "ec.jei.kitGalacticraft");
		registerItem(registry, ItemHelper.itemKit, ItemCardType.KIT_GENERATOR, "ec.jei.kitGenerator");
		registerItem(registry, ItemHelper.itemKit, ItemCardType.KIT_LIQUID, "ec.jei.kitLiquid");
		registerItem(registry, ItemHelper.itemKit, ItemCardType.KIT_LIQUID_ADVANCED, "ec.jei.kitLiquidAdv");
		registerItem(registry, ItemHelper.itemKit, ItemCardType.KIT_REACTOR, "ec.jei.kitReactor");
		registerItem(registry, ItemHelper.itemKit, ItemCardType.KIT_TOGGLE, "ec.jei.kitToggle");
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
