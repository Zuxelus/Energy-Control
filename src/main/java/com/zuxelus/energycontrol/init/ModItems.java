package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.*;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.items.*;
import com.zuxelus.energycontrol.items.cards.ItemCardHolder;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.tileentities.*;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

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
	public static Block blockTimer;

	public static Block blockAfsu;
	public static Block blockIc2Cable;
	public static Block blockSeedAnalyzer;
	public static Block blockSeedLibrary;

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

	public static void onBlockRegistry() {
		blockLight = register(new BlockLight(), ItemLight.class, "block_light");
		blockHowlerAlarm = register(new HowlerAlarm(), "howler_alarm");
		blockIndustrialAlarm = register(new IndustrialAlarm(), "industrial_alarm");
		blockThermalMonitor = register(new ThermalMonitor(), "thermal_monitor");
		blockInfoPanel = register(new InfoPanel(), TileEntityInfoPanel.NAME);
		blockInfoPanelExtender = register(new InfoPanelExtender(), "info_panel_extender");
		blockInfoPanelAdvanced = register(new AdvancedInfoPanel(), TileEntityAdvancedInfoPanel.NAME);
		blockInfoPanelAdvancedExtender = register(new AdvancedInfoPanelExtender(), "info_panel_advanced_extender");
		blockHoloPanel = register(new HoloPanel(), "holo_panel");
		blockHoloPanelExtender = register(new HoloPanelExtender(), "holo_panel_extender");
		blockRangeTrigger = register(new RangeTrigger(), "range_trigger");
		blockRemoteThermo = register(new RemoteThermalMonitor(), "remote_thermo");
		blockAverageCounter = register(new AverageCounter(), "average_counter");
		blockEnergyCounter = register(new EnergyCounter(), "energy_counter");
		blockKitAssembler = register(new KitAssembler(), "kit_assembler");
		blockTimer = register(new TimerBlock(), "timer");
	}

	public static void onItemRegistry() {
		itemUpgrade = register(new ItemUpgrade(), "item_upgrade");
		itemThermometer = register(new ItemThermometer(), "thermometer");
		itemPortablePanel = register(new ItemPortablePanel(), "portable_panel");

		itemKit = new ItemKitMain();
		((ItemKitMain) itemKit).registerKits();
		register(itemKit, "item_kit");

		itemCard = new ItemCardMain();
		((ItemCardMain) itemCard).registerCards();
		register(itemCard, "item_card");

		itemCardHolder = register(new ItemCardHolder(), "card_holder");
		itemComponent = register(new ItemComponent(), "item_component");

		CrossModLoader.registerItems(); // In 1.10.2 in EnergyControl class

		OreDictionary.registerOre("circuitBasic", new ItemStack(itemComponent, 1, ItemComponent.BASIC_CIRCUIT));
		OreDictionary.registerOre("circuitAdvanced", new ItemStack(itemComponent, 1, ItemComponent.ADVANCED_CIRCUIT));
	}

	public static Block register(Block block, String name) {
		return register(block, ItemBase.class, name);
	}

	public static Block register(Block block, Class<? extends ItemBlock> itemClass, String name) {
		block.setBlockName(name);
		GameRegistry.registerBlock(block, itemClass, name);
		return block;
	}

	public static Item register(Item item, String name) {
		item.setUnlocalizedName(name);
		GameRegistry.registerItem(item, name);
		return item;
	}

	public static void registerTileEntities() {
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
}
