package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blockentities.*;
import com.zuxelus.energycontrol.blocks.*;
import com.zuxelus.energycontrol.containers.*;
import com.zuxelus.energycontrol.items.*;
import com.zuxelus.energycontrol.items.cards.*;
import com.zuxelus.energycontrol.items.kits.*;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class ModItems {
	public static final Item FABRIC_ITEM = new Item(new Item.Settings().group(EnergyControl.ITEM_GROUP));
	public static final Identifier CARD_HOLDER = new Identifier(EnergyControl.MODID, "card_holder");
	public static final Item CARD_HOLDER_ITEM = new CardHolderItem();
	public static final Identifier PORTABLE_PANEL = new Identifier(EnergyControl.MODID, "portable_panel");
	public static final Item PORTABLE_PANEL_ITEM = new PortablePanelItem();
	public static final Item UPGRADE_RANGE_ITEM = new UpgradeRangeItem();
	public static final Item UPGRADE_COLOR_ITEM = new UpgradeColorItem();
	public static final Item UPGRADE_TOUCH_ITEM = new UpgradeTouchItem();
	
	public static final Item NANO_BOW_ITEM = new NanoBowTRItem();
	public static final Item ENERGY_ITEM_CARD = new EnergyItemCard();
	public static final Item GENERATOR_ITEM_CARD = new GeneratorItemCard();
	public static final Item LIQUID_ADVANCED_ITEM_CARD = new LiquidAdvancedItemCard();
	public static final Item LIQUID_ITEM_CARD = new LiquidItemCard();
	public static final Item TIME_ITEM_CARD = new TimeItemCard();
	public static final Item TEXT_ITEM_CARD = new TextItemCard();
	public static final Item TOGGLE_ITEM_CARD = new ToggleItemCard();

	public static final Item ENERGY_ITEM_KIT = new EnergyItemKit();
	public static final Item GENERATOR_ITEM_KIT = new GeneratorItemKit();
	public static final Item LIQUID_ADVANCED_ITEM_KIT = new LiquidAdvancedItemKit();
	public static final Item LIQUID_ITEM_KIT = new LiquidItemKit();
	public static final Item TOGGLE_ITEM_KIT = new ToggleItemKit();

	public static final Block WHITE_LAMP_BLOCK = new LightBlock();
	public static final Block ORANGE_LAMP_BLOCK = new LightBlock();

	public static final Block HOWLER_ALARM_BLOCK = new HowlerAlarmBlock();
	public static final Identifier HOWLER_ALARM = new Identifier(EnergyControl.MODID, "howler_alarm");
	public static final Block INDUSTRIAL_ALARM_BLOCK = new IndustrialAlarmBlock();
	public static final Identifier INDUSTRIAL_ALARM = new Identifier(EnergyControl.MODID, "industrial_alarm");

	public static final Block INFO_PANEL_BLOCK = new InfoPanelBlock();
	public static final Identifier INFO_PANEL = new Identifier(EnergyControl.MODID, "info_panel");
	public static final String INFO_PANEL_TRANSLATION_KEY = Util.createTranslationKey("container", INFO_PANEL);

	public static final Block INFO_PANEL_EXTENDER_BLOCK = new InfoPanelExtenderBlock();
	public static final Identifier INFO_PANEL_EXTENDER = new Identifier(EnergyControl.MODID, "info_panel_extender");
	
	public static final Block AVERAGE_COUNTER_BLOCK = new AverageCounterBlock();
	public static final Identifier AVERAGE_COUNTER = new Identifier(EnergyControl.MODID, "average_counter");
	public static final Block ENERGY_COUNTER_BLOCK = new EnergyCounterBlock();
	public static final Identifier ENERGY_COUNTER = new Identifier(EnergyControl.MODID, "energy_counter");
	
	public static BlockEntityType<HowlerAlarmBlockEntity> HOWLER_ALARM_BLOCK_ENTITY;
	public static BlockEntityType<IndustrialAlarmBlockEntity> INDUSTRIAL_ALARM_BLOCK_ENTITY;
	public static BlockEntityType<InfoPanelBlockEntity> INFO_PANEL_BLOCK_ENTITY;
	public static BlockEntityType<InfoPanelExtenderBlockEntity> INFO_PANEL_EXTENDER_BLOCK_ENTITY;

	public static void init() {
		registerBlocks();
		registerItems();
		registerBlockEntities();
		registerContainers();
	}

	private static void registerBlocks() {
		Registry.register(Registry.BLOCK, new Identifier(EnergyControl.MODID, "white_lamp"), WHITE_LAMP_BLOCK);
		Registry.register(Registry.BLOCK, new Identifier(EnergyControl.MODID, "orange_lamp"), ORANGE_LAMP_BLOCK);
		Registry.register(Registry.BLOCK, HOWLER_ALARM, HOWLER_ALARM_BLOCK);
		Registry.register(Registry.BLOCK, INDUSTRIAL_ALARM, INDUSTRIAL_ALARM_BLOCK);
		Registry.register(Registry.BLOCK, INFO_PANEL, INFO_PANEL_BLOCK);
		Registry.register(Registry.BLOCK, INFO_PANEL_EXTENDER, INFO_PANEL_EXTENDER_BLOCK);
		Registry.register(Registry.BLOCK, AVERAGE_COUNTER, AVERAGE_COUNTER_BLOCK);
		Registry.register(Registry.BLOCK, ENERGY_COUNTER, ENERGY_COUNTER_BLOCK);
	}

	private static void registerItems() {
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID, "white_lamp"), new BlockItem(WHITE_LAMP_BLOCK, new Item.Settings().group(EnergyControl.ITEM_GROUP)));
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID, "orange_lamp"), new BlockItem(ORANGE_LAMP_BLOCK, new Item.Settings().group(EnergyControl.ITEM_GROUP)));
		Registry.register(Registry.ITEM, HOWLER_ALARM, new BlockItem(HOWLER_ALARM_BLOCK, new Item.Settings().group(EnergyControl.ITEM_GROUP)));
		Registry.register(Registry.ITEM, INDUSTRIAL_ALARM, new BlockItem(INDUSTRIAL_ALARM_BLOCK, new Item.Settings().group(EnergyControl.ITEM_GROUP)));
		Registry.register(Registry.ITEM, INFO_PANEL, new BlockItem(INFO_PANEL_BLOCK, new Item.Settings().group(EnergyControl.ITEM_GROUP)));
		Registry.register(Registry.ITEM, INFO_PANEL_EXTENDER, new BlockItem(INFO_PANEL_EXTENDER_BLOCK, new Item.Settings().group(EnergyControl.ITEM_GROUP)));
		Registry.register(Registry.ITEM, AVERAGE_COUNTER, new BlockItem(AVERAGE_COUNTER_BLOCK, new Item.Settings().group(EnergyControl.ITEM_GROUP)));
		Registry.register(Registry.ITEM, ENERGY_COUNTER, new BlockItem(ENERGY_COUNTER_BLOCK, new Item.Settings().group(EnergyControl.ITEM_GROUP)));

		Registry.register(Registry.ITEM, CARD_HOLDER, CARD_HOLDER_ITEM);
		Registry.register(Registry.ITEM, PORTABLE_PANEL, PORTABLE_PANEL_ITEM);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":upgrade_range"), UPGRADE_RANGE_ITEM);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":upgrade_color"), UPGRADE_COLOR_ITEM);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":upgrade_touch"), UPGRADE_TOUCH_ITEM);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":nano_bow"), NANO_BOW_ITEM);

		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":card_energy"), ENERGY_ITEM_CARD);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":card_generator"), GENERATOR_ITEM_CARD);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":card_liquid_advanced"), LIQUID_ADVANCED_ITEM_CARD);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":card_liquid"), LIQUID_ITEM_CARD);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":card_time"), TIME_ITEM_CARD);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":card_text"), TEXT_ITEM_CARD);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":card_toggle"), TOGGLE_ITEM_CARD);

		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":kit_energy"), ENERGY_ITEM_KIT);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":kit_generator"), GENERATOR_ITEM_KIT);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":kit_liquid_advanced"), LIQUID_ADVANCED_ITEM_KIT);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":kit_liquid"), LIQUID_ITEM_KIT);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":kit_toggle"), TOGGLE_ITEM_KIT);
	}

	private static void registerBlockEntities() {
		INFO_PANEL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, INFO_PANEL.toString(), BlockEntityType.Builder.create(InfoPanelBlockEntity::new, INFO_PANEL_BLOCK).build(null));
		INFO_PANEL_EXTENDER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, INFO_PANEL_EXTENDER.toString(), BlockEntityType.Builder.create(InfoPanelExtenderBlockEntity::new, INFO_PANEL_EXTENDER_BLOCK).build(null));
		HOWLER_ALARM_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, HOWLER_ALARM.toString(), BlockEntityType.Builder.create(HowlerAlarmBlockEntity::new, HOWLER_ALARM_BLOCK).build(null));
		INDUSTRIAL_ALARM_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, INDUSTRIAL_ALARM.toString(), BlockEntityType.Builder.create(IndustrialAlarmBlockEntity::new, INDUSTRIAL_ALARM_BLOCK).build(null));
	}

	private static void registerContainers() {
		ContainerProviderRegistry.INSTANCE.registerFactory(INFO_PANEL, (syncId, identifier, player, buf) -> {
			final BlockEntity be = player.world.getBlockEntity(buf.readBlockPos());
			return new InfoPanelContainer(syncId, player.inventory, (InfoPanelBlockEntity) be);
		});
		ContainerProviderRegistry.INSTANCE.registerFactory(CARD_HOLDER, (syncId, identifier, player, buf) -> {
			return new CardHolderContainer(syncId, player);
		});
		ContainerProviderRegistry.INSTANCE.registerFactory(PORTABLE_PANEL, (syncId, identifier, player, buf) -> {
			return new PortablePanelContainer(syncId, player);
		});
	}
}
