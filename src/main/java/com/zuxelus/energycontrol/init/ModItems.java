package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.*;
import com.zuxelus.energycontrol.items.*;
import com.zuxelus.energycontrol.items.cards.*;
import com.zuxelus.energycontrol.items.kits.*;
import com.zuxelus.energycontrol.recipes.ArrayRecipeSerializer;
import com.zuxelus.energycontrol.recipes.StorageArrayRecipe;

import mekanism.common.registration.impl.IRecipeSerializerDeferredRegister;
import mekanism.common.registration.impl.IRecipeSerializerRegistryObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EnergyControl.MODID);
	public static final RegistryObject<Block> white_lamp = BLOCKS.register("white_lamp", () -> new BlockLight());
	public static final RegistryObject<Block> orange_lamp = BLOCKS.register("orange_lamp", () -> new BlockLight());
	public static final RegistryObject<Block> howler_alarm = BLOCKS.register("howler_alarm", () -> new HowlerAlarm());
	public static final RegistryObject<Block> industrial_alarm = BLOCKS.register("industrial_alarm", () -> new IndustrialAlarm());
	public static final RegistryObject<Block> thermal_monitor = BLOCKS.register("thermal_monitor", () -> new ThermalMonitor());
	public static final RegistryObject<Block> range_trigger = BLOCKS.register("range_trigger", () -> new RangeTrigger());
	public static final RegistryObject<Block> info_panel = BLOCKS.register("info_panel", () -> new InfoPanel());
	public static final RegistryObject<Block> info_panel_extender = BLOCKS.register("info_panel_extender", () -> new InfoPanelExtender());
	public static final RegistryObject<Block> info_panel_advanced = BLOCKS.register("info_panel_advanced", () -> new AdvancedInfoPanel());
	public static final RegistryObject<Block> info_panel_advanced_extender = BLOCKS.register("info_panel_advanced_extender", () -> new AdvancedInfoPanelExtender());
	public static final RegistryObject<Block> holo_panel = BLOCKS.register("holo_panel", () -> new HoloPanel());
	public static final RegistryObject<Block> holo_panel_extender = BLOCKS.register("holo_panel_extender", () -> new HoloPanelExtender());
	//public static final RegistryObject<Block> average_counter = BLOCKS.register("average_counter", () -> new AverageCounter());
	//public static final RegistryObject<Block> energy_counter = BLOCKS.register("energy_counter", () -> new EnergyCounter());
	public static final RegistryObject<Block> kit_assembler = BLOCKS.register("kit_assembler", () -> new KitAssembler());
	public static final RegistryObject<Block> timer = BLOCKS.register("timer", () -> new TimerBlock());

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EnergyControl.MODID);
	public static final RegistryObject<Item> kit_energy = ITEMS.register("kit_energy", () -> new ItemKitEnergy());
	public static final RegistryObject<Item> kit_inventory = ITEMS.register("kit_inventory", () -> new ItemKitInventory());
	public static final RegistryObject<Item> kit_liquid = ITEMS.register("kit_liquid", () -> new ItemKitLiquid());
	public static final RegistryObject<Item> kit_liquid_advanced = ITEMS.register("kit_liquid_advanced", () -> new ItemKitLiquidAdvanced());
	public static final RegistryObject<Item> kit_mekanism = ITEMS.register("kit_mekanism", () -> new ItemKitMekanism());
	public static final RegistryObject<Item> kit_big_reactors = ITEMS.register("kit_big_reactors", () -> new ItemKitBigReactors());
	public static final RegistryObject<Item> kit_redstone = ITEMS.register("kit_redstone", () -> new ItemKitRedstone());
	public static final RegistryObject<Item> kit_toggle = ITEMS.register("kit_toggle", () -> new ItemKitToggle());
	public static Item kit_app_eng;
	public static final RegistryObject<Item> card_holder = ITEMS.register("card_holder", () -> new ItemCardHolder());
	public static final RegistryObject<Item> card_energy = ITEMS.register("card_energy", () -> new ItemCardEnergy());
	public static final RegistryObject<Item> card_energy_array = ITEMS.register("card_energy_array", () -> new ItemCardEnergyArray());
	public static final RegistryObject<Item> card_inventory = ITEMS.register("card_inventory", () -> new ItemCardInventory());
	public static final RegistryObject<Item> card_liquid = ITEMS.register("card_liquid", () -> new ItemCardLiquid());
	public static final RegistryObject<Item> card_liquid_advanced = ITEMS.register("card_liquid_advanced", () -> new ItemCardLiquidAdvanced());
	public static final RegistryObject<Item> card_liquid_array = ITEMS.register("card_liquid_array", () -> new ItemCardLiquidArray());
	public static final RegistryObject<Item> card_mekanism = ITEMS.register("card_mekanism", () -> new ItemCardMekanism());
	public static final RegistryObject<Item> card_big_reactors = ITEMS.register("card_big_reactors", () -> new ItemCardBigReactors());
	public static final RegistryObject<Item> card_redstone = ITEMS.register("card_redstone", () -> new ItemCardRedstone());
	public static final RegistryObject<Item> card_text = ITEMS.register("card_text", () -> new ItemCardText());
	public static final RegistryObject<Item> card_time = ITEMS.register("card_time", () -> new ItemCardTime());
	public static final RegistryObject<Item> card_toggle = ITEMS.register("card_toggle", () -> new ItemCardToggle());
	public static Item card_app_eng;
	public static Item card_app_eng_inv;
	public static final RegistryObject<Item> upgrade_range = ITEMS.register("upgrade_range", () -> new Item(new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
	public static final RegistryObject<Item> upgrade_color = ITEMS.register("upgrade_color", () -> new Item(new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
	public static final RegistryObject<Item> upgrade_touch = ITEMS.register("upgrade_touch", () -> new Item(new Item.Properties().tab(EnergyControl.ITEM_GROUP)));
	public static final RegistryObject<Item> portable_panel = ITEMS.register("portable_panel", () -> new ItemPortablePanel());

	public static final IRecipeSerializerDeferredRegister RECIPE_SERIALIZERS = new IRecipeSerializerDeferredRegister(EnergyControl.MODID);
	public static final IRecipeSerializerRegistryObject<StorageArrayRecipe> ARRAY = RECIPE_SERIALIZERS.register("array", ArrayRecipeSerializer::new);
}
