package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.items.cards.*;
import com.zuxelus.energycontrol.items.kits.*;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = EnergyControl.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler {

	@SubscribeEvent
	public static void onItemRegistry(Register<Item> event) {
		event.getRegistry().register(new BlockItem(ModItems.white_lamp.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("white_lamp"));
		event.getRegistry().register(new BlockItem(ModItems.orange_lamp.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("orange_lamp"));
		event.getRegistry().register(new BlockItem(ModItems.howler_alarm.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("howler_alarm"));
		event.getRegistry().register(new BlockItem(ModItems.industrial_alarm.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("industrial_alarm"));
		event.getRegistry().register(new BlockItem(ModItems.thermal_monitor.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("thermal_monitor"));
		event.getRegistry().register(new BlockItem(ModItems.info_panel.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName(TileEntityInfoPanel.NAME));
		event.getRegistry().register(new BlockItem(ModItems.info_panel_extender.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("info_panel_extender"));
		event.getRegistry().register(new BlockItem(ModItems.info_panel_advanced.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName(TileEntityAdvancedInfoPanel.NAME));
		event.getRegistry().register(new BlockItem(ModItems.info_panel_advanced_extender.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("info_panel_advanced_extender"));
		event.getRegistry().register(new BlockItem(ModItems.holo_panel.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("holo_panel"));
		event.getRegistry().register(new BlockItem(ModItems.holo_panel_extender.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("holo_panel_extender"));
		event.getRegistry().register(new BlockItem(ModItems.range_trigger.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("range_trigger"));
		event.getRegistry().register(new BlockItem(ModItems.remote_thermo.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("remote_thermo"));
		event.getRegistry().register(new BlockItem(ModItems.kit_assembler.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("kit_assembler"));
		event.getRegistry().register(new BlockItem(ModItems.timer.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)).setRegistryName("timer"));

		ModList list = ModList.get();
		/*if (list.isLoaded(ModIDs.APPLIED_ENERGISTICS)) {
			ModItems.kit_app_eng = new ItemKitAppEng().setRegistryName("kit_app_eng");
			event.getRegistry().register(ModItems.kit_app_eng);
			ModItems.card_app_eng = new ItemCardAppEng().setRegistryName("card_app_eng");
			event.getRegistry().register(ModItems.card_app_eng);
			ModItems.card_app_eng_inv = new ItemCardAppEngInv().setRegistryName("card_app_eng_inv");
			event.getRegistry().register(ModItems.card_app_eng_inv);
		}*/
		if (list.isLoaded(ModIDs.BIG_REACTORS) || list.isLoaded(ModIDs.BIGGER_REACTORS)) {
			ModItems.kit_big_reactors = new ItemKitBigReactors().setRegistryName("kit_big_reactors");
			event.getRegistry().register(ModItems.kit_big_reactors);
			ModItems.card_big_reactors = new ItemCardBigReactors().setRegistryName("card_big_reactors");
			event.getRegistry().register(ModItems.card_big_reactors);
		}
		/*if (list.isLoaded(ModIDs.IMMERSIVE_ENGINEERING)) {
			ModItems.kit_immersive_engineering = new ItemKitImmersiveEngineering().setRegistryName("kit_immersive_engineering");
			event.getRegistry().register(ModItems.kit_immersive_engineering);
			ModItems.card_immersive_engineering = new ItemCardImmersiveEngineering().setRegistryName("card_immersive_engineering");
			event.getRegistry().register(ModItems.card_immersive_engineering);
		}
		if (list.isLoaded(ModIDs.MEKANISM)) {
			ModItems.kit_mekanism = new ItemKitMekanism().setRegistryName("kit_mekanism");
			event.getRegistry().register(ModItems.kit_mekanism);
			ModItems.card_mekanism = new ItemCardMekanism().setRegistryName("card_mekanism");
			event.getRegistry().register(ModItems.card_mekanism);
		}
		if (list.isLoaded(ModIDs.THERMAL_EXPANSION)) {
			ModItems.kit_thermal_expansion = new ItemKitThermalExpansion().setRegistryName("kit_thermal_expansion");
			event.getRegistry().register(ModItems.kit_thermal_expansion);
			ModItems.card_thermal_expansion = new ItemCardThermalExpansion().setRegistryName("card_thermal_expansion");
			event.getRegistry().register(ModItems.card_thermal_expansion);
		}*/
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(Register<RecipeSerializer<?>> event) {
		Registry.register(Registry.RECIPE_TYPE, "kit_assembler", KitAssemblerRecipeType.TYPE);
	}
}
