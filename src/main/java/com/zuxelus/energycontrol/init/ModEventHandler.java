package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.items.ItemLight;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = EnergyControl.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler {

	@SubscribeEvent
	public static void onItemRegistry(Register<Item> event) {
		event.getRegistry().register(new ItemLight(ModItems.white_lamp.get()).setRegistryName("white_lamp"));
		event.getRegistry().register(new ItemLight(ModItems.orange_lamp.get()).setRegistryName("orange_lamp"));
		event.getRegistry().register(new BlockItem(ModItems.howler_alarm.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("howler_alarm"));
		event.getRegistry().register(new BlockItem(ModItems.industrial_alarm.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("industrial_alarm"));
		event.getRegistry().register(new BlockItem(ModItems.thermal_monitor.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("thermal_monitor"));
		event.getRegistry().register(new BlockItem(ModItems.info_panel.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName(TileEntityInfoPanel.NAME));
		event.getRegistry().register(new BlockItem(ModItems.info_panel_extender.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("info_panel_extender"));
		event.getRegistry().register(new BlockItem(ModItems.info_panel_advanced.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName(TileEntityAdvancedInfoPanel.NAME));
		event.getRegistry().register(new BlockItem(ModItems.info_panel_advanced_extender.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("info_panel_advanced_extender"));
		event.getRegistry().register(new BlockItem(ModItems.range_trigger.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("range_trigger"));
		event.getRegistry().register(new BlockItem(ModItems.kit_assembler.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("kit_assembler"));
		event.getRegistry().register(new BlockItem(ModItems.timer.get(), new Item.Properties().group(EnergyControl.ITEM_GROUP)).setRegistryName("timer"));
	}
}
