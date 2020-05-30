package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.BlockLight;
import com.zuxelus.energycontrol.blocks.BlockMain;
import com.zuxelus.energycontrol.items.cards.ItemCardHolder;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.tileentities.*;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class ItemHelper {
	public static BlockLight blockLight;
	public static BlockMain blockMain;
	public static Item itemKit;
	public static Item itemCard;
	public static Item itemUpgrade;
	public static Item itemThermometer;
	public static Item itemThermometerDigital;
	public static Item itemPortablePanel;
	public static Item itemCardHolder;

	public static void onBlockRegistry() {
		blockLight = new BlockLight();
		setNames(blockLight, "block_light");
		GameRegistry.registerBlock(blockLight, ItemLight.class,"block_light");

		blockMain = new BlockMain();
		setNames(blockMain, "block_main");
		GameRegistry.registerBlock(blockMain, ItemMain.class, "block_main");
	}

	public static void onItemRegistry() {
		itemUpgrade = new ItemUpgrade();
		setNames(itemUpgrade, "item_upgrade");
		GameRegistry.registerItem(itemUpgrade, "item_upgrade");

		itemThermometer = new ItemThermometer();
		setNames(itemThermometer, "thermometer");
		GameRegistry.registerItem(itemThermometer, "thermometer");

		itemThermometerDigital = new ItemDigitalThermometer(1, 80, 80);
		setNames(itemThermometerDigital, "thermometer_digital");
		GameRegistry.registerItem(itemThermometerDigital, "thermometer_digital");

		itemPortablePanel = new ItemPortablePanel();
		setNames(itemPortablePanel, "portable_panel");
		GameRegistry.registerItem(itemPortablePanel, "portable_panel");

		itemKit = new ItemKitMain();
		((ItemKitMain) itemKit).registerKits();
		setNames(itemKit, "item_kit");
		GameRegistry.registerItem(itemKit, "item_kit");

		itemCard = new ItemCardMain();
		((ItemCardMain) itemCard).registerCards();
		setNames(itemCard, "item_card");
		GameRegistry.registerItem(itemCard, "item_card");

		itemCardHolder = new ItemCardHolder();
		setNames(itemCardHolder, "card_holder");
		GameRegistry.registerItem(itemCardHolder, "card_holder");
	}

	private static void setNames(Object obj, String name) {
		if (obj instanceof Block) {
			Block block = (Block) obj;
			//block.setUnlocalizedName(name); TODO
			//block.setRegistryName(name);
		} else if (obj instanceof Item) {
			Item item = (Item) obj;
			item.setUnlocalizedName(name);
			//item.setRegistryName(name);
		} else
			throw new IllegalArgumentException("Item or Block required");
	}

	public static void registerTileEntities() { // TODO Change to event
		GameRegistry.registerTileEntity(TileEntityHowlerAlarm.class, EnergyControl.MODID + ":howler_alarm");
		GameRegistry.registerTileEntity(TileEntityIndustrialAlarm.class, EnergyControl.MODID + ":industrial_alarm");
		GameRegistry.registerTileEntity(TileEntityThermo.class, EnergyControl.MODID + ":thermo");
		GameRegistry.registerTileEntity(TileEntityRemoteThermo.class, EnergyControl.MODID + ":remote_thermo");
		GameRegistry.registerTileEntity(TileEntityInfoPanel.class, EnergyControl.MODID + ":" + TileEntityInfoPanel.NAME);
		GameRegistry.registerTileEntity(TileEntityInfoPanelExtender.class, EnergyControl.MODID + ":info_panel_extender");
		GameRegistry.registerTileEntity(TileEntityAdvancedInfoPanel.class, EnergyControl.MODID + ":" + TileEntityAdvancedInfoPanel.NAME);
		GameRegistry.registerTileEntity(TileEntityAdvancedInfoPanelExtender.class, EnergyControl.MODID + ":info_panel_advanced_extender");
		GameRegistry.registerTileEntity(TileEntityRangeTrigger.class, EnergyControl.MODID + ":range_trigger");
		GameRegistry.registerTileEntity(TileEntityAverageCounter.class, EnergyControl.MODID + ":average_counter");
		GameRegistry.registerTileEntity(TileEntityEnergyCounter.class, EnergyControl.MODID + ":energy_counter");
		GameRegistry.registerTileEntity(TileEntityKitAssembler.class, EnergyControl.MODID + ":kit_assembler");
	}
}
