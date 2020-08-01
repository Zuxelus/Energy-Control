package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blockentities.InfoPanelBlockEntity;
import com.zuxelus.energycontrol.blockentities.InfoPanelExtenderBlockEntity;
import com.zuxelus.energycontrol.blocks.InfoPanelBlock;
import com.zuxelus.energycontrol.blocks.InfoPanelExtenderBlock;
import com.zuxelus.energycontrol.blocks.LightBlock;
import com.zuxelus.energycontrol.containers.InfoPanelContainer;
import com.zuxelus.energycontrol.items.UpgradeColorItem;
import com.zuxelus.energycontrol.items.UpgradeRangeItem;
import com.zuxelus.energycontrol.items.UpgradeTouchItem;
import com.zuxelus.energycontrol.items.cards.EnergyItemCard;
import com.zuxelus.energycontrol.items.cards.TextItemCard;
import com.zuxelus.energycontrol.items.cards.TimeItemCard;
import com.zuxelus.energycontrol.items.kits.EnergyItemKit;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class ModItems {
	public static final Item FABRIC_ITEM = new Item(new Item.Settings().group(EnergyControl.ITEM_GROUP));
	public static final Item UPGRADE_RANGE_ITEM = new UpgradeRangeItem();
	public static final Item UPGRADE_COLOR_ITEM = new UpgradeColorItem();
	public static final Item UPGRADE_TOUCH_ITEM = new UpgradeTouchItem();
	public static final Item ENERGY_ITEM_CARD = new EnergyItemCard();
	public static final Item TIME_ITEM_CARD = new TimeItemCard();
	public static final Item TEXT_ITEM_CARD = new TextItemCard();
	
	public static final Item ENERGY_ITEM_KIT = new EnergyItemKit();
	
	public static final Block WHITE_LAMP_BLOCK = new LightBlock();
	public static final Block ORANGE_LAMP_BLOCK = new LightBlock();
	
	public static final Block INFO_PANEL_BLOCK = new InfoPanelBlock();
	public static final Identifier INFO_PANEL = new Identifier(EnergyControl.MODID, "info_panel");
	public static final String INFO_PANEL_TRANSLATION_KEY = Util.createTranslationKey("container", INFO_PANEL);

	public static final Block INFO_PANEL_EXTENDER_BLOCK = new InfoPanelExtenderBlock();
	public static final Identifier INFO_PANEL_EXTENDER = new Identifier(EnergyControl.MODID, "info_panel_extender");
	
	public static BlockEntityType<InfoPanelBlockEntity> INFO_PANEL_BLOCK_ENTITY;
	public static BlockEntityType<InfoPanelExtenderBlockEntity> INFO_PANEL_EXTENDER_BLOCK_ENTITY;

	public static void init() {
		registerBlocks();
		registerItems();
		registerBlockEntities();
	}

	private static void registerBlocks() {
		Registry.register(Registry.BLOCK, new Identifier(EnergyControl.MODID + ":white_lamp"), WHITE_LAMP_BLOCK);
		Registry.register(Registry.BLOCK, new Identifier(EnergyControl.MODID + ":orange_lamp"), ORANGE_LAMP_BLOCK);
		Registry.register(Registry.BLOCK, INFO_PANEL, INFO_PANEL_BLOCK);
		Registry.register(Registry.BLOCK, INFO_PANEL_EXTENDER, INFO_PANEL_EXTENDER_BLOCK);
	}

	private static void registerItems() {
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":white_lamp"), new BlockItem(WHITE_LAMP_BLOCK, new Item.Settings().group(EnergyControl.ITEM_GROUP)));
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":orange_lamp"), new BlockItem(ORANGE_LAMP_BLOCK, new Item.Settings().group(EnergyControl.ITEM_GROUP)));
		Registry.register(Registry.ITEM, INFO_PANEL, new BlockItem(INFO_PANEL_BLOCK, new Item.Settings().group(EnergyControl.ITEM_GROUP)));
		Registry.register(Registry.ITEM, INFO_PANEL_EXTENDER, new BlockItem(INFO_PANEL_EXTENDER_BLOCK, new Item.Settings().group(EnergyControl.ITEM_GROUP)));
		
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":card_holder"), FABRIC_ITEM);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":upgrade_range"), UPGRADE_RANGE_ITEM);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":upgrade_color"), UPGRADE_COLOR_ITEM);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":upgrade_touch"), UPGRADE_TOUCH_ITEM);
		
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":card_energy"), ENERGY_ITEM_CARD);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":card_time"), TIME_ITEM_CARD);
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":card_text"), TEXT_ITEM_CARD);
		
		Registry.register(Registry.ITEM, new Identifier(EnergyControl.MODID + ":kit_energy"), ENERGY_ITEM_KIT);
	}

	private static void registerBlockEntities() {
		INFO_PANEL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, INFO_PANEL.toString(), BlockEntityType.Builder.create(InfoPanelBlockEntity::new, INFO_PANEL_BLOCK).build(null));
		ContainerProviderRegistry.INSTANCE.registerFactory(INFO_PANEL, (syncId, identifier, player, buf) -> {
			final BlockEntity be = player.world.getBlockEntity(buf.readBlockPos());
			return new InfoPanelContainer(syncId, player.inventory, (InfoPanelBlockEntity) be);
		});
		INFO_PANEL_EXTENDER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, INFO_PANEL_EXTENDER.toString(), BlockEntityType.Builder.create(InfoPanelExtenderBlockEntity::new, INFO_PANEL_EXTENDER_BLOCK).build(null));
	}
}
