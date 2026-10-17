package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.*;
import com.zuxelus.energycontrol.items.*;
import com.zuxelus.energycontrol.items.cards.*;
import com.zuxelus.energycontrol.items.kits.*;
import com.zuxelus.energycontrol.recipes.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModItems {
	// a new instance each time: block settings are mutable and must not be shared between blocks
	public static BlockBehaviour.Properties blockSettings() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F);
	}
	public static final Block white_lamp = new BlockLight(MapColor.SNOW);
	public static final Block orange_lamp = new BlockLight(MapColor.COLOR_ORANGE);
	public static final Block howler_alarm = new HowlerAlarm();
	public static final Block industrial_alarm = new IndustrialAlarm();
	public static final Block thermal_monitor = new ThermalMonitor();
	public static final Block range_trigger = new RangeTrigger();
	public static final Block remote_thermo = new RemoteThermalMonitor();
	public static final Block info_panel = new InfoPanel();
	public static final Block info_panel_extender = new InfoPanelExtender();
	public static final Block info_panel_advanced = new AdvancedInfoPanel();
	public static final Block info_panel_advanced_extender = new AdvancedInfoPanelExtender();
	public static final Block holo_panel = new HoloPanel();
	public static final Block holo_panel_extender = new HoloPanelExtender();
	//public static final Block average_counter = new AverageCounter();
	//public static final Block energy_counter = new EnergyCounter();
	public static final Block kit_assembler = new KitAssembler();
	public static final Block timer = new TimerBlock();

	public static final Item kit_energy = new ItemKitEnergy();
	public static final Item kit_inventory = new ItemKitInventory();
	public static final Item kit_liquid = new ItemKitLiquid();
	public static final Item kit_liquid_advanced = new ItemKitLiquidAdvanced();
	public static final Item kit_redstone = new ItemKitRedstone();
	public static final Item kit_toggle = new ItemKitToggle();
	public static Item kit_app_eng;
	public static Item kit_big_reactors;
	public static Item kit_immersive_engineering;
	public static Item kit_mekanism;
	public static Item kit_thermal_expansion;
	public static final Item card_holder = new ItemCardHolder();
	public static final Item card_energy = new ItemCardEnergy();
	public static final Item card_energy_array = new ItemCardEnergyArray();
	public static final Item card_inventory = new ItemCardInventory();
	public static final Item card_liquid = new ItemCardLiquid();
	public static final Item card_liquid_advanced = new ItemCardLiquidAdvanced();
	public static final Item card_liquid_array = new ItemCardLiquidArray();
	public static final Item card_redstone = new ItemCardRedstone();
	public static final Item card_text = new ItemCardText();
	public static final Item card_time = new ItemCardTime();
	public static final Item card_toggle = new ItemCardToggle();
	public static Item card_app_eng;
	public static Item card_app_eng_inv;
	public static Item card_big_reactors;
	public static Item card_immersive_engineering;
	public static Item card_mekanism;
	public static Item card_thermal_expansion;
	public static final Item upgrade_range = new Item(new Item.Properties());
	public static final Item upgrade_color = new Item(new Item.Properties());
	public static final Item upgrade_touch = new Item(new Item.Properties());
	public static final Item upgrade_web = new Item(new Item.Properties());
	public static final Item portable_panel = new ItemPortablePanel();
	public static final Item machine_casing = new Item(new Item.Properties());
	public static final Item basic_circuit = new Item(new Item.Properties());
	public static final Item advanced_circuit = new Item(new Item.Properties());
	public static final Item radio_transmitter = new Item(new Item.Properties());
	public static final Item strong_string = new Item(new Item.Properties());

	public static RecipeSerializer<StorageArrayRecipe> ARRAY_SERIALIZER;
	public static RecipeSerializer<KitAssemblerRecipe> KIT_ASSEMBLER_SERIALIZER;

	public static void init() {
		registerBlocks();
		registerItems();
		register();
	}

	private static void registerBlocks() {
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "white_lamp"), white_lamp);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "orange_lamp"), orange_lamp);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "howler_alarm"), howler_alarm);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "industrial_alarm"), industrial_alarm);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "thermal_monitor"), thermal_monitor);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "range_trigger"), range_trigger);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "remote_thermo"), remote_thermo);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "info_panel"), info_panel);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "info_panel_extender"), info_panel_extender);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "info_panel_advanced"), info_panel_advanced);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "info_panel_advanced_extender"), info_panel_advanced_extender);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "holo_panel"), holo_panel);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "holo_panel_extender"), holo_panel_extender);
		//Registry.register(Registries.BLOCK, Identifier.of(EnergyControl.MODID, "average_counter"), average_counter);
		//Registry.register(Registries.BLOCK, Identifier.of(EnergyControl.MODID, "energy_counter"), energy_counter);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "kit_assembler"), kit_assembler);
		Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "timer"), timer);
	}

	private static void registerItems() {
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "white_lamp"), new BlockItem(white_lamp, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "orange_lamp"), new BlockItem(orange_lamp, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "howler_alarm"), new BlockItem(howler_alarm, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "industrial_alarm"), new BlockItem(industrial_alarm, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "thermal_monitor"), new BlockItem(thermal_monitor, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "info_panel"), new BlockItem(info_panel, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "info_panel_extender"), new BlockItem(info_panel_extender, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "info_panel_advanced"), new BlockItem(info_panel_advanced, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "info_panel_advanced_extender"), new BlockItem(info_panel_advanced_extender, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "holo_panel"), new BlockItem(holo_panel, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "holo_panel_extender"), new BlockItem(holo_panel_extender, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "range_trigger"), new BlockItem(range_trigger, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "remote_thermo"), new BlockItem(remote_thermo, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "kit_assembler"), new BlockItem(kit_assembler, new Item.Properties()));
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "timer"), new BlockItem(timer, new Item.Properties()));

		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "kit_energy"), kit_energy);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "kit_inventory"), kit_inventory);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "kit_liquid"), kit_liquid);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "kit_liquid_advanced"), kit_liquid_advanced);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "kit_redstone"), kit_redstone);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "kit_toggle"), kit_toggle);

		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "card_holder"), card_holder);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "card_energy"), card_energy);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "card_energy_array"), card_energy_array);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "card_inventory"), card_inventory);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "card_liquid"), card_liquid);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "card_liquid_advanced"), card_liquid_advanced);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "card_liquid_array"), card_liquid_array);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "card_redstone"), card_redstone);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "card_text"), card_text);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "card_time"), card_time);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "card_toggle"), card_toggle);

		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "upgrade_range"), upgrade_range);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "upgrade_color"), upgrade_color);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "upgrade_touch"), upgrade_touch);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "upgrade_web"), upgrade_web);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "portable_panel"), portable_panel);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "machine_casing"), machine_casing);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "basic_circuit"), basic_circuit);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "advanced_circuit"), advanced_circuit);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "radio_transmitter"), radio_transmitter);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "strong_string"), strong_string);
	}

	private static void register() {
		ARRAY_SERIALIZER = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "array"), new ArrayRecipeSerializer());
		KIT_ASSEMBLER_SERIALIZER = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "kit_assembler"), new KitAssemblerSerializer());
		Registry.register(BuiltInRegistries.RECIPE_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "kit_assembler"), KitAssemblerRecipeType.TYPE);
	}
}
