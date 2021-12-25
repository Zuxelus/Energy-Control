package com.zuxelus.energycontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.tileentities.ScreenManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class EnergyControl implements ModInitializer {
	public static final String MODID = "energycontrol";
	public static EnergyControl INSTANCE;
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static ScreenManager screenManager = new ScreenManager();

	@Environment(EnvType.CLIENT)
	public List<String> availableAlarms; //on client
	@Environment(EnvType.CLIENT)
	public List<String> serverAllowedAlarms; // will be loaded from server

	public static Map<PlayerEntity, Boolean> altPressed = new HashMap<PlayerEntity, Boolean>();

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
			new Identifier(MODID, "general"), () -> new ItemStack(ModItems.kit_energy));

	@Override
	public void onInitialize() {
		INSTANCE = this;
		new ConfigHandler();
		new ModContainerTypes();
		ModItems.init();
		ChannelHandler.init();
		CrossModLoader.init();
	}
}
