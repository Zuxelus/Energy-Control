package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.items.cards.*;
import com.zuxelus.energycontrol.items.kits.*;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@EventBusSubscriber(modid = EnergyControl.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler {

	@SubscribeEvent
	public static void onRegisters(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.ITEMS, ModEventHandler::onItemRegistry);
		event.register(ForgeRegistries.Keys.RECIPE_TYPES, register -> {
			register.register("kit_assembler", KitAssemblerRecipeType.TYPE);
		});
	}

	public static void onItemRegistry(RegisterEvent.RegisterHelper<Item> event) {
		event.register("white_lamp", new BlockItem(ModItems.white_lamp.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register("orange_lamp", new BlockItem(ModItems.orange_lamp.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register("howler_alarm", new BlockItem(ModItems.howler_alarm.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register("industrial_alarm", new BlockItem(ModItems.industrial_alarm.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register("thermal_monitor", new BlockItem(ModItems.thermal_monitor.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register(TileEntityInfoPanel.NAME, new BlockItem(ModItems.info_panel.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register("info_panel_extender", new BlockItem(ModItems.info_panel_extender.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register(TileEntityAdvancedInfoPanel.NAME, new BlockItem(ModItems.info_panel_advanced.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register("info_panel_advanced_extender", new BlockItem(ModItems.info_panel_advanced_extender.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register("holo_panel", new BlockItem(ModItems.holo_panel.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register("holo_panel_extender", new BlockItem(ModItems.holo_panel_extender.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register("range_trigger", new BlockItem(ModItems.range_trigger.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register("remote_thermo", new BlockItem(ModItems.remote_thermo.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register("kit_assembler", new BlockItem(ModItems.kit_assembler.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
		event.register("timer", new BlockItem(ModItems.timer.get(), new Item.Properties().tab(EnergyControl.ITEM_GROUP)));

		ModList list = ModList.get();
		if (list.isLoaded(ModIDs.ADV_GENERATORS)) {
			ModItems.kit_adv_generators = new ItemKitAdvGenerators();
			event.register("kit_adv_generators", ModItems.kit_adv_generators);
			ModItems.card_adv_generators = new ItemCardAdvGenerators();
			event.register("card_adv_generators", ModItems.card_adv_generators);
		}
		if (list.isLoaded(ModIDs.APPLIED_ENERGISTICS)) {
			ModItems.kit_app_eng = new ItemKitAppEng();
			event.register("kit_app_eng", ModItems.kit_app_eng);
			ModItems.card_app_eng = new ItemCardAppEng();
			event.register("card_app_eng", ModItems.card_app_eng);
			ModItems.card_app_eng_inv = new ItemCardAppEngInv();
			event.register("card_app_eng_inv", ModItems.card_app_eng_inv);
		}
		if (list.isLoaded(ModIDs.BIG_REACTORS) || list.isLoaded(ModIDs.BIGGER_REACTORS)) {
			ModItems.kit_big_reactors = new ItemKitBigReactors();
			event.register("kit_big_reactors", ModItems.kit_big_reactors);
			ModItems.card_big_reactors = new ItemCardBigReactors();
			event.register("card_big_reactors", ModItems.card_big_reactors);
		}
		/*if (list.isLoaded(ModIDs.IMMERSIVE_ENGINEERING)) {
			ModItems.kit_immersive_engineering = new ItemKitImmersiveEngineering();
			event.register("kit_immersive_engineering", ModItems.kit_immersive_engineering);
			ModItems.card_immersive_engineering = new ItemCardImmersiveEngineering();
			event.register("card_immersive_engineering", ModItems.card_immersive_engineering);
		}*/
		if (list.isLoaded(ModIDs.MEKANISM)) {
			ModItems.kit_mekanism = new ItemKitMekanism();
			event.register("kit_mekanism", ModItems.kit_mekanism);
			ModItems.card_mekanism = new ItemCardMekanism();
			event.register("card_mekanism", ModItems.card_mekanism);
		}
		/*if (list.isLoaded(ModIDs.THERMAL_EXPANSION)) {
			ModItems.kit_thermal_expansion = new ItemKitThermalExpansion();
			event.register("kit_thermal_expansion", ModItems.kit_thermal_expansion);
			ModItems.card_thermal_expansion = new ItemCardThermalExpansion();
			event.register("card_thermal_expansion", ModItems.card_thermal_expansion);
		}*/
	}
}
