package com.zuxelus.energycontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zuxelus.energycontrol.blockentities.ScreenManager;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.recipes.NanoBowRecipeTR;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EnergyControl implements ModInitializer {
	public static final String MODID = "energycontrol";
	public static EnergyControl INSTANCE;
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static ScreenManager screenManager = new ScreenManager();

	@Environment(EnvType.CLIENT)
	public List<String> availableAlarms; //on client
	@Environment(EnvType.CLIENT)
	public List<String> serverAllowedAlarms; // will be loaded from server

	public static Map<UUID, Boolean> modeSwitchKeyPressed = new HashMap<UUID, Boolean>();
	public static Map<UUID, Boolean> altPressed = new HashMap<UUID, Boolean>();

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
			new Identifier(MODID, "general"), () -> new ItemStack(ModItems.ENERGY_ITEM_KIT));

	@Override
	public void onInitialize() {
		INSTANCE = this;
		new ConfigHandler();
		ModItems.init();
		ChannelHandler.init();
		Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "nano_bow"), new NanoBowRecipeTR.Serializer());
	}
}
