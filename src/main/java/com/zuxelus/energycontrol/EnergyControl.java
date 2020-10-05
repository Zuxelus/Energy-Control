package com.zuxelus.energycontrol;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zuxelus.energycontrol.blockentities.ScreenManager;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.network.ChannelHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
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

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
			new Identifier(MODID, "general"), () -> new ItemStack(ModItems.ENERGY_ITEM_KIT));

	@Override
	public void onInitialize() {
		INSTANCE = this;
		ModItems.init();
		ChannelHandler.init();
	}
}
