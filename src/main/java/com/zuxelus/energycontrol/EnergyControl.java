package com.zuxelus.energycontrol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zuxelus.energycontrol.blockentities.InfoPanelBlockEntity;
import com.zuxelus.energycontrol.blockentities.ScreenManager;
import com.zuxelus.energycontrol.blocks.InfoPanelBlock;
import com.zuxelus.energycontrol.blocks.LightBlock;
import com.zuxelus.energycontrol.containers.InfoPanelContainer;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.UpgradeColorItem;
import com.zuxelus.energycontrol.items.cards.TextItemCard;
import com.zuxelus.energycontrol.items.cards.TimeItemCard;
import com.zuxelus.energycontrol.items.kits.EnergyItemKit;
import com.zuxelus.energycontrol.network.ChannelHandler;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class EnergyControl implements ModInitializer {
	public static final String MODID = "energycontrol";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public static ScreenManager screenManager = new ScreenManager();
	
	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
			new Identifier(MODID, "general"), () -> new ItemStack(ModItems.ENERGY_ITEM_KIT));

	@Override
	public void onInitialize() {
		ModItems.init();
		ChannelHandler.init();
	}
}
