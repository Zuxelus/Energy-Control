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
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.text.Text;

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
			BuiltinClientPlugin.getInstance().registerInformation(EntryStacks.of(item), item.asItem().getName(), lines -> {
				lines.add(Text.translatable(key));
				return lines;
			});
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