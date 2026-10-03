package com.zuxelus.energycontrol;

import java.util.Comparator;
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
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
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

	public static ItemGroup ITEM_GROUP;

	@Override
	public void onInitialize() {
		INSTANCE = this;
		new ConfigHandler();
		new ModContainerTypes();
		ModItems.init();
		ITEM_GROUP = FabricItemGroup.builder(new Identifier(MODID, "general"))
			.icon(() -> new ItemStack(ModItems.kit_energy))
			.entries((context, entries) -> Registries.ITEM.getIds().stream()
				.filter(id -> id.getNamespace().equals(MODID))
				.sorted(Comparator.comparingInt(id -> Registries.ITEM.getRawId(Registries.ITEM.get(id))))
				.forEach(id -> entries.add(Registries.ITEM.get(id))))
			.build();
		ChannelHandler.init();
		CrossModLoader.init();
	}
}
