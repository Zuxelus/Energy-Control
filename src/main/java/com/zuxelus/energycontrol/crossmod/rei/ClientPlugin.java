package com.zuxelus.energycontrol.crossmod.rei;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.GuiKitAssembler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BuiltinPlugin;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemConvertible;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class ClientPlugin implements REIPluginV0 {
	private static final Identifier ID = new Identifier(EnergyControl.MODID, "rei_plugin");

	@Override
	public Identifier getPluginIdentifier() {
		return ID;
	}

	@Override
	public void registerPluginCategories(RecipeHelper helper) {
		helper.registerCategory(new KitAssemblerRecipeCategory());
	}

	@Override
	public void registerRecipeDisplays(RecipeHelper helper) {
		helper.registerRecipes(KitAssemblerRecipeCategory.id, KitAssemblerRecipe.class, KitAssemblerDisplay::new);
		registerInformation();
	}

	// REI's "Information" pages, the same texts as JEI's ingredient info
	private static void registerInformation() {
		registerItem(ModItems.white_lamp, "ec.jei.blockLightWhite");
		registerItem(ModItems.orange_lamp, "ec.jei.blockLightOrange");
		registerItem(ModItems.howler_alarm, "ec.jei.blockHowlerAlarm");
		registerItem(ModItems.industrial_alarm, "ec.jei.blockIndustrialAlarm");
		registerItem(ModItems.thermal_monitor, "ec.jei.blockThermalMonitor");
		registerItem(ModItems.info_panel, "ec.jei.blockInfoPanel");
		registerItem(ModItems.info_panel_extender, "ec.jei.blockInfoPanelExtender");
		registerItem(ModItems.info_panel_advanced, "ec.jei.blockInfoPanelAdvanced");
		registerItem(ModItems.info_panel_advanced_extender, "ec.jei.blockInfoPanelAdvancedExtender");
		registerItem(ModItems.holo_panel, "ec.jei.blockHoloPanel");
		registerItem(ModItems.kit_assembler, "ec.jei.blockKitAssembler");

		registerItem(ModItems.upgrade_color, "ec.jei.upgradeColor");
		registerItem(ModItems.upgrade_range, "ec.jei.upgradeRange");
		registerItem(ModItems.upgrade_touch, "ec.jei.upgradeTouch");

		registerItem(ModItems.kit_energy, "ec.jei.kitEnergy");
		registerItem(ModItems.kit_liquid, "ec.jei.kitLiquid");
		registerItem(ModItems.kit_liquid_advanced, "ec.jei.kitLiquidAdv");
		registerItem(ModItems.kit_toggle, "ec.jei.kitToggle");

		registerItem(ModItems.card_energy, "ec.jei.cards");
		registerItem(ModItems.card_inventory, "ec.jei.cards");
		registerItem(ModItems.card_liquid_advanced, "ec.jei.cards");
		registerItem(ModItems.card_liquid, "ec.jei.cards");
		registerItem(ModItems.card_redstone, "ec.jei.cards");
		registerItem(ModItems.card_toggle, "ec.jei.cards");

		registerItem(ModItems.card_holder, "ec.jei.itemCardHolder");
		registerItem(ModItems.portable_panel, "ec.jei.itemPortablePanel");
	}

	private static void registerItem(ItemConvertible item, String key) {
		if (item != null)
			BuiltinPlugin.getInstance().registerInformation(EntryStack.create(item), item.asItem().getName(), lines -> {
				lines.add(new TranslatableText(key));
				return lines;
			});
	}

	@Override
	public void registerOthers(RecipeHelper helper) {
		helper.registerWorkingStations(KitAssemblerRecipeCategory.id, EntryStack.create(ModItems.kit_assembler));
		helper.removeAutoCraftButton(KitAssemblerRecipeCategory.id);
		helper.registerContainerClickArea(new Rectangle(87, 35, 22, 15), GuiKitAssembler.class, KitAssemblerRecipeCategory.id);
		// REI's default handler moves only one item per slot, recipes here need several
		helper.registerAutoCraftingHandler(new KitAssemblerTransferHandler());
	}
}
