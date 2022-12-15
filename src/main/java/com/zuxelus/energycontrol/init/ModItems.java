package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.*;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.items.*;
import com.zuxelus.energycontrol.items.cards.ItemCardHolder;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.recipes.StorageArrayRecipe;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber
public class ModItems {
	public static Block blockLight;
	public static Block blockHowlerAlarm;
	public static Block blockIndustrialAlarm;
	public static Block blockThermalMonitor;
	public static Block blockInfoPanel;
	public static Block blockInfoPanelExtender;
	public static Block blockInfoPanelAdvanced;
	public static Block blockInfoPanelAdvancedExtender;
	public static Block blockHoloPanel;
	public static Block blockHoloPanelExtender;
	public static Block blockRangeTrigger;
	public static Block blockRemoteThermo;
	public static Block blockAverageCounter;
	public static Block blockEnergyCounter;
	public static Block blockKitAssembler;
	public static Block blockAfsu;
	public static Block blockIc2Cable;
	public static Block blockSeedAnalyzer;
	public static Block blockSeedLibrary;
	public static Block blockTimer;
	public static Item itemKit;
	public static Item itemCard;
	public static Item itemUpgrade;
	public static Item itemThermometer;
	public static Item itemThermometerDigital;
	public static Item itemPortablePanel;
	public static Item itemCardHolder;
	public static Item itemComponent;
	public static Item itemAFB;
	public static Item itemAFSUUpgradeKit;
	public static Item itemPanelToolkit;

	@SubscribeEvent
	public static void onBlockRegistry(Register<Block> event) {
		blockLight = register(event, new BlockLight(), "block_light");
		blockHowlerAlarm = register(event, new HowlerAlarm(), "howler_alarm");
		blockIndustrialAlarm = register(event, new IndustrialAlarm(), "industrial_alarm");
		blockThermalMonitor = register(event, new ThermalMonitor(), "thermal_monitor");
		blockInfoPanel = register(event, new InfoPanel(), TileEntityInfoPanel.NAME);
		blockInfoPanelExtender = register(event, new InfoPanelExtender(), "info_panel_extender");
		blockInfoPanelAdvanced = register(event, new AdvancedInfoPanel(), TileEntityAdvancedInfoPanel.NAME);
		blockInfoPanelAdvancedExtender = register(event, new AdvancedInfoPanelExtender(), "info_panel_advanced_extender");
		blockHoloPanel = register(event, new HoloPanel(), "holo_panel");
		blockHoloPanelExtender = register(event, new HoloPanelExtender(), "holo_panel_extender");
		blockRangeTrigger = register(event, new RangeTrigger(), "range_trigger");
		blockRemoteThermo = register(event, new RemoteThermalMonitor(), "remote_thermo");
		if (Loader.isModLoaded(ModIDs.IC2)) {
			blockAverageCounter = register(event, new AverageCounter(), "average_counter");
			blockEnergyCounter = register(event, new EnergyCounter(), "energy_counter");
		}
		blockKitAssembler = register(event, new KitAssembler(), "kit_assembler");
		blockTimer = register(event, new TimerBlock(), "timer");

		CrossModLoader.registerBlocks(event);
	}

	@SubscribeEvent
	public static void onItemRegistry(Register<Item> event) {
		event.getRegistry().register(new ItemLight(blockLight).setRegistryName("block_light"));
		event.getRegistry().register(new ItemBlock(blockHowlerAlarm).setRegistryName("howler_alarm"));
		event.getRegistry().register(new ItemBlock(blockIndustrialAlarm).setRegistryName("industrial_alarm"));
		event.getRegistry().register(new ItemBlock(blockThermalMonitor).setRegistryName("thermal_monitor"));
		event.getRegistry().register(new ItemBlock(blockInfoPanel).setRegistryName(TileEntityInfoPanel.NAME));
		event.getRegistry().register(new ItemBlock(blockInfoPanelExtender).setRegistryName("info_panel_extender"));
		event.getRegistry().register(new ItemBlock(blockHoloPanel).setRegistryName("holo_panel"));
		event.getRegistry().register(new ItemBlock(blockHoloPanelExtender).setRegistryName("holo_panel_extender"));
		event.getRegistry().register(new ItemBlock(blockInfoPanelAdvanced).setRegistryName(TileEntityAdvancedInfoPanel.NAME));
		event.getRegistry().register(new ItemBlock(blockInfoPanelAdvancedExtender).setRegistryName("info_panel_advanced_extender"));
		event.getRegistry().register(new ItemBlock(blockRangeTrigger).setRegistryName("range_trigger"));
		event.getRegistry().register(new ItemBlock(blockRemoteThermo).setRegistryName("remote_thermo"));
		if (Loader.isModLoaded(ModIDs.IC2)) {
			event.getRegistry().register(new ItemBlock(blockAverageCounter).setRegistryName("average_counter"));
			event.getRegistry().register(new ItemBlock(blockEnergyCounter).setRegistryName("energy_counter"));
		}
		event.getRegistry().register(new ItemBlock(blockKitAssembler).setRegistryName("kit_assembler"));
		event.getRegistry().register(new ItemBlock(blockTimer).setRegistryName("timer"));

		itemUpgrade = register(event, new ItemUpgrade(), "item_upgrade");
		itemThermometer = register(event, new ItemThermometer(), "thermometer");
		itemPortablePanel = register(event, new ItemPortablePanel(), "portable_panel");
		itemPanelToolkit = register(event, new ItemPanelToolkit(), "panel_toolkit");

		itemKit = new ItemKitMain();
		((ItemKitMain) itemKit).registerKits();
		register(event, itemKit, "item_kit");

		itemCard = new ItemCardMain();
		((ItemCardMain) itemCard).registerCards();
		register(event, itemCard, "item_card");

		itemCardHolder = register(event, new ItemCardHolder(), "card_holder");
		itemComponent = register(event, new ItemComponent(), "item_component");

		OreDictionary.registerOre("circuitBasic", new ItemStack(itemComponent, 1, ItemComponent.BASIC_CIRCUIT));
		OreDictionary.registerOre("circuitAdvanced", new ItemStack(itemComponent, 1, ItemComponent.ADVANCED_CIRCUIT));

		CrossModLoader.registerItems(event);
	}

	public static Block register(Register<Block> event, Block block, String name) {
		block.setUnlocalizedName(name);
		block.setRegistryName(name);
		event.getRegistry().register(block);
		return block;
	}

	public static Item register(Register<Item> event, Item item, String name) {
		item.setUnlocalizedName(name);
		item.setRegistryName(name);
		event.getRegistry().register(item);
		return item;
	}

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		registerBlockModel(blockLight, BlockLight.DAMAGE_WHITE_OFF, "lamp0");
		registerBlockModel(blockLight, BlockLight.DAMAGE_WHITE_ON, "lamp1");
		registerBlockModel(blockLight, BlockLight.DAMAGE_ORANGE_OFF, "lamp2");
		registerBlockModel(blockLight, BlockLight.DAMAGE_ORANGE_ON, "lamp3");

		registerBlockModel(blockHowlerAlarm, 0, "howler_alarm");
		registerBlockModel(blockIndustrialAlarm, 0, "industrial_alarm");
		registerBlockModel(blockThermalMonitor, 0, "thermal_monitor");
		registerBlockModel(blockRemoteThermo, 0, "remote_thermo");
		if (Loader.isModLoaded(ModIDs.IC2)) {
			registerBlockModel(blockAverageCounter, 0, "average_counter");
			registerBlockModel(blockEnergyCounter, 0, "energy_counter");
		}
		registerBlockModel(blockInfoPanel, 0, TileEntityInfoPanel.NAME);
		registerBlockModel(blockInfoPanelExtender, 0, "info_panel_extender");
		registerBlockModel(blockInfoPanelAdvanced, 0, TileEntityAdvancedInfoPanel.NAME);
		registerBlockModel(blockInfoPanelAdvancedExtender, 0, "info_panel_advanced_extender");
		registerBlockModel(blockHoloPanel, 0, "holo_panel");
		registerBlockModel(blockHoloPanelExtender, 0, "holo_panel_extender");
		registerBlockModel(blockRangeTrigger, 0, "range_trigger");
		registerBlockModel(blockKitAssembler, 0, "kit_assembler");
		registerBlockModel(ModItems.blockTimer, 0, "timer");

		ItemKitMain.registerModels();
		ItemKitMain.registerExtendedModels();
		ItemCardMain.registerModels();
		ItemCardMain.registerExtendedModels();

		registerItemModel(itemUpgrade, ItemUpgrade.DAMAGE_RANGE, "upgrade_range");
		registerItemModel(itemUpgrade, ItemUpgrade.DAMAGE_COLOR, "upgrade_color");
		registerItemModel(itemUpgrade, ItemUpgrade.DAMAGE_TOUCH, "upgrade_touch");
		registerItemModel(itemUpgrade, ItemUpgrade.DAMAGE_WEB, "upgrade_web");
		registerItemModel(itemThermometer, 0, "thermometer");
		registerItemModel(itemPortablePanel, 0, "portable_panel");
		registerItemModel(itemCardHolder, 0, "card_holder");
		registerItemModel(itemPanelToolkit, 0, "panel_toolkit");
		registerItemModel(itemComponent, ItemComponent.ADVANCED_CIRCUIT, "advanced_circuit");
		registerItemModel(itemComponent, ItemComponent.BASIC_CIRCUIT, "basic_circuit");
		registerItemModel(itemComponent, ItemComponent.MACHINE_CASING, "machine_casing");
		registerItemModel(itemComponent, ItemComponent.RADIO_TRANSMITTER, "radio_transmitter");
		registerItemModel(itemComponent, ItemComponent.STRONG_STRING, "strong_string");
		CrossModLoader.registerModels(event);
	}

	public static void registerItemModel(Item item, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(EnergyControl.MODID + ":" + name, "inventory"));
	}

	public static void registerExternalItemModel(Item item, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name, "inventory"));
	}

	public static void registerBlockModel(Block block, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(EnergyControl.MODID + ":" + name, "inventory"));
	}

	@SuppressWarnings("deprecation")
	public static void registerTileEntities() { // TODO Change to event
		GameRegistry.registerTileEntity(TileEntityHowlerAlarm.class, EnergyControl.MODID + ":howler_alarm");
		GameRegistry.registerTileEntity(TileEntityIndustrialAlarm.class, EnergyControl.MODID + ":industrial_alarm");
		GameRegistry.registerTileEntity(TileEntityThermalMonitor.class, EnergyControl.MODID + ":thermo");
		GameRegistry.registerTileEntity(TileEntityRemoteThermalMonitor.class, EnergyControl.MODID + ":remote_thermo");
		GameRegistry.registerTileEntity(TileEntityInfoPanel.class, EnergyControl.MODID + ":" + TileEntityInfoPanel.NAME);
		GameRegistry.registerTileEntity(TileEntityInfoPanelExtender.class, EnergyControl.MODID + ":info_panel_extender");
		GameRegistry.registerTileEntity(TileEntityAdvancedInfoPanel.class, EnergyControl.MODID + ":" + TileEntityAdvancedInfoPanel.NAME);
		GameRegistry.registerTileEntity(TileEntityAdvancedInfoPanelExtender.class, EnergyControl.MODID + ":info_panel_advanced_extender");
		GameRegistry.registerTileEntity(TileEntityHoloPanel.class, EnergyControl.MODID + ":holo_panel");
		GameRegistry.registerTileEntity(TileEntityHoloPanelExtender.class, EnergyControl.MODID + ":holo_panel_extender");
		GameRegistry.registerTileEntity(TileEntityRangeTrigger.class, EnergyControl.MODID + ":range_trigger");
		GameRegistry.registerTileEntity(TileEntityAverageCounter.class, EnergyControl.MODID + ":average_counter");
		GameRegistry.registerTileEntity(TileEntityEnergyCounter.class, EnergyControl.MODID + ":energy_counter");
		GameRegistry.registerTileEntity(TileEntityKitAssembler.class, EnergyControl.MODID + ":kit_assembler");
		GameRegistry.registerTileEntity(TileEntityTimer.class, EnergyControl.MODID + ":timer");

		CrossModLoader.registerTileEntities();
	}

	@SubscribeEvent
	public static void registerRecipes(Register<IRecipe> event) {
		event.getRegistry().register(new StorageArrayRecipe().setRegistryName("array_card_recipe"));
	}
}
