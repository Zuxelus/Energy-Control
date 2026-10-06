package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.*;
import com.zuxelus.energycontrol.items.*;
import com.zuxelus.energycontrol.items.cards.*;
import com.zuxelus.energycontrol.items.kits.*;
import com.zuxelus.energycontrol.recipes.*;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModItems {
	private static ResourceKey<Block> blockKey;
	private static ResourceKey<Item> itemKey;

	// a new instance each time: block settings are mutable and must not be shared between blocks.
	// Since 1.21.2 the registry key has to be set before the block is constructed, see block(...)
	public static BlockBehaviour.Properties blockSettings() {
		return BlockBehaviour.Properties.of().setId(blockKey).mapColor(MapColor.METAL).strength(3.0F);
	}

	public static BlockBehaviour.Properties emptyBlockSettings() {
		return BlockBehaviour.Properties.of().setId(blockKey);
	}

	public static Item.Properties itemSettings() {
		return new Item.Properties().setId(itemKey);
	}

	public static final Block white_lamp = block("white_lamp", BlockLight::new);
	public static final Block orange_lamp = block("orange_lamp", BlockLight::new);
	public static final Block howler_alarm = block("howler_alarm", HowlerAlarm::new);
	public static final Block industrial_alarm = block("industrial_alarm", IndustrialAlarm::new);
	public static final Block thermal_monitor = block("thermal_monitor", ThermalMonitor::new);
	public static final Block range_trigger = block("range_trigger", RangeTrigger::new);
	public static final Block remote_thermo = block("remote_thermo", RemoteThermalMonitor::new);
	public static final Block info_panel = block("info_panel", InfoPanel::new);
	public static final Block info_panel_extender = block("info_panel_extender", InfoPanelExtender::new);
	public static final Block info_panel_advanced = block("info_panel_advanced", AdvancedInfoPanel::new);
	public static final Block info_panel_advanced_extender = block("info_panel_advanced_extender", AdvancedInfoPanelExtender::new);
	public static final Block holo_panel = block("holo_panel", HoloPanel::new);
	public static final Block holo_panel_extender = block("holo_panel_extender", HoloPanelExtender::new);
	public static final Block kit_assembler = block("kit_assembler", KitAssembler::new);
	public static final Block timer = block("timer", TimerBlock::new);
	//public static final Block average_counter = block("average_counter", AverageCounter::new);
	//public static final Block energy_counter = block("energy_counter", EnergyCounter::new);

	public static final Item white_lamp_item = blockItem(white_lamp);
	public static final Item orange_lamp_item = blockItem(orange_lamp);
	public static final Item howler_alarm_item = blockItem(howler_alarm);
	public static final Item industrial_alarm_item = blockItem(industrial_alarm);
	public static final Item thermal_monitor_item = blockItem(thermal_monitor);
	public static final Item info_panel_item = blockItem(info_panel);
	public static final Item info_panel_extender_item = blockItem(info_panel_extender);
	public static final Item info_panel_advanced_item = blockItem(info_panel_advanced);
	public static final Item info_panel_advanced_extender_item = blockItem(info_panel_advanced_extender);
	public static final Item holo_panel_item = blockItem(holo_panel);
	public static final Item holo_panel_extender_item = blockItem(holo_panel_extender);
	public static final Item range_trigger_item = blockItem(range_trigger);
	public static final Item remote_thermo_item = blockItem(remote_thermo);
	public static final Item kit_assembler_item = blockItem(kit_assembler);
	public static final Item timer_item = blockItem(timer);

	public static final Item kit_energy = item("kit_energy", ItemKitEnergy::new);
	public static final Item kit_inventory = item("kit_inventory", ItemKitInventory::new);
	public static final Item kit_liquid = item("kit_liquid", ItemKitLiquid::new);
	public static final Item kit_liquid_advanced = item("kit_liquid_advanced", ItemKitLiquidAdvanced::new);
	public static final Item kit_redstone = item("kit_redstone", ItemKitRedstone::new);
	public static final Item kit_toggle = item("kit_toggle", ItemKitToggle::new);
	public static Item kit_app_eng;
	public static Item kit_big_reactors;
	public static Item kit_immersive_engineering;
	public static Item kit_mekanism;
	public static Item kit_thermal_expansion;
	public static final Item card_holder = item("card_holder", ItemCardHolder::new);
	public static final Item card_energy = item("card_energy", ItemCardEnergy::new);
	public static final Item card_energy_array = item("card_energy_array", ItemCardEnergyArray::new);
	public static final Item card_inventory = item("card_inventory", ItemCardInventory::new);
	public static final Item card_liquid = item("card_liquid", ItemCardLiquid::new);
	public static final Item card_liquid_advanced = item("card_liquid_advanced", ItemCardLiquidAdvanced::new);
	public static final Item card_liquid_array = item("card_liquid_array", ItemCardLiquidArray::new);
	public static final Item card_redstone = item("card_redstone", ItemCardRedstone::new);
	public static final Item card_text = item("card_text", ItemCardText::new);
	public static final Item card_time = item("card_time", ItemCardTime::new);
	public static final Item card_toggle = item("card_toggle", ItemCardToggle::new);
	public static Item card_app_eng;
	public static Item card_app_eng_inv;
	public static Item card_big_reactors;
	public static Item card_immersive_engineering;
	public static Item card_mekanism;
	public static Item card_thermal_expansion;
	public static final Item upgrade_range = item("upgrade_range", () -> new Item(itemSettings()));
	public static final Item upgrade_color = item("upgrade_color", () -> new Item(itemSettings()));
	public static final Item upgrade_touch = item("upgrade_touch", () -> new Item(itemSettings()));
	public static final Item portable_panel = item("portable_panel", ItemPortablePanel::new);
	public static final Item machine_casing = item("machine_casing", () -> new Item(itemSettings()));
	public static final Item basic_circuit = item("basic_circuit", () -> new Item(itemSettings()));
	public static final Item advanced_circuit = item("advanced_circuit", () -> new Item(itemSettings()));
	public static final Item radio_transmitter = item("radio_transmitter", () -> new Item(itemSettings()));
	public static final Item strong_string = item("strong_string", () -> new Item(itemSettings()));

	public static RecipeSerializer<StorageArrayRecipe> ARRAY_SERIALIZER;
	public static RecipeSerializer<KitAssemblerRecipe> KIT_ASSEMBLER_SERIALIZER;

	// loads the class, which registers all blocks and items above
	public static void init() {
		register();
	}

	private static <T extends Block> T block(String name, Supplier<T> factory) {
		blockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(EnergyControl.MODID, name));
		return Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.get());
	}

	public static <T extends Item> T item(String name, Supplier<T> factory) {
		itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EnergyControl.MODID, name));
		return Registry.register(BuiltInRegistries.ITEM, itemKey, factory.get());
	}

	private static Item blockItem(Block block) {
		return item(BuiltInRegistries.BLOCK.getKey(block).getPath(), () -> new BlockItem(block, itemSettings().useBlockDescriptionPrefix()));
	}

	private static void register() {
		ARRAY_SERIALIZER = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "array"), ArrayRecipeSerializer.create());
		KIT_ASSEMBLER_SERIALIZER = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "kit_assembler"), KitAssemblerSerializer.create());
		Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "kit_assembler"), KitAssemblerRecipeType.TYPE);
	}
}
