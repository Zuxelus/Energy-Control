package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.items.cards.*;
import com.zuxelus.energycontrol.items.kits.*;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = EnergyControl.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler {

	@SubscribeEvent
	public static void onItemRegistry(Register<Item> event) {
		event.getRegistry().register(new BlockItem(ModItems.white_lamp.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("white_lamp"));
		event.getRegistry().register(new BlockItem(ModItems.orange_lamp.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("orange_lamp"));
		event.getRegistry().register(new BlockItem(ModItems.howler_alarm.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("howler_alarm"));
		event.getRegistry().register(new BlockItem(ModItems.industrial_alarm.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("industrial_alarm"));
		event.getRegistry().register(new BlockItem(ModItems.thermal_monitor.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("thermal_monitor"));
		event.getRegistry().register(new BlockItem(ModItems.info_panel.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName(TileEntityInfoPanel.NAME));
		event.getRegistry().register(new BlockItem(ModItems.info_panel_extender.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("info_panel_extender"));
		event.getRegistry().register(new BlockItem(ModItems.info_panel_advanced.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName(TileEntityAdvancedInfoPanel.NAME));
		event.getRegistry().register(new BlockItem(ModItems.info_panel_advanced_extender.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("info_panel_advanced_extender"));
		event.getRegistry().register(new BlockItem(ModItems.holo_panel.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("holo_panel"));
		event.getRegistry().register(new BlockItem(ModItems.holo_panel_extender.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("holo_panel_extender"));
		event.getRegistry().register(new BlockItem(ModItems.range_trigger.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("range_trigger"));
		event.getRegistry().register(new BlockItem(ModItems.kit_assembler.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("kit_assembler"));
		event.getRegistry().register(new BlockItem(ModItems.timer.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("timer"));

		ModList list = ModList.get();
		if (list.isLoaded(ModIDs.APPLIED_ENERGISTICS)) {
			ModItems.kit_app_eng = new ItemKitAppEng().setRegistryName("kit_app_eng");
			event.getRegistry().register(ModItems.kit_app_eng);
			ModItems.card_app_eng = new ItemCardAppEng().setRegistryName("card_app_eng");
			event.getRegistry().register(ModItems.card_app_eng);
			ModItems.card_app_eng_inv = new ItemCardAppEngInv().setRegistryName("card_app_eng_inv");
			event.getRegistry().register(ModItems.card_app_eng_inv);
		}
		if (list.isLoaded(ModIDs.MEKANISM)) {
			ModItems.kit_mekanism = new ItemKitMekanism().setRegistryName("kit_mekanism");
			event.getRegistry().register(ModItems.kit_mekanism);
			ModItems.card_mekanism = new ItemCardMekanism().setRegistryName("card_mekanism");
			event.getRegistry().register(ModItems.card_mekanism);
		}
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(Register<IRecipeSerializer<?>> event) {
		Registry.register(Registry.RECIPE_TYPE, "kit_assembler", KitAssemblerRecipeType.TYPE);
	}
}
