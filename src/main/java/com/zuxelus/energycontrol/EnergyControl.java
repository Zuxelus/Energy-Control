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
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.tileentities.ScreenManager;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class EnergyControl implements ModInitializer {
	public static final String MODID = "energycontrol";
	public static EnergyControl INSTANCE;
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static ScreenManager screenManager = new ScreenManager();

	@Environment(EnvType.CLIENT)
	public List<String> availableAlarms; //on client
	@Environment(EnvType.CLIENT)
	public List<String> serverAllowedAlarms; // will be loaded from server

	public static Map<Player, Boolean> altPressed = new HashMap<Player, Boolean>();

	public static CreativeModeTab ITEM_GROUP;

	@Override
	public void onInitialize() {
		INSTANCE = this;
		new ConfigHandler();
		new ModContainerTypes();
		ModItems.init();
		ITEM_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(MODID, "general"), FabricCreativeModeTab.builder()
			.title(Component.translatable("itemGroup.energycontrol.general"))
			.icon(() -> new ItemStack(ModItems.kit_energy))
			.displayItems((context, entries) -> BuiltInRegistries.ITEM.keySet().stream()
				.filter(id -> id.getNamespace().equals(MODID))
				.sorted(Comparator.comparingInt(id -> BuiltInRegistries.ITEM.getId(BuiltInRegistries.ITEM.getValue(id))))
				.forEach(id -> entries.accept(BuiltInRegistries.ITEM.getValue(id))))
			.build());
		// also loads ModTileEntityTypes, so the block entity types are registered while the registries are open
		team.reborn.energy.api.EnergyStorage.SIDED.registerForBlockEntity(TileEntityKitAssembler::getEnergyInput, ModTileEntityTypes.kit_assembler);
		ChannelHandler.init();
		CrossModLoader.init();
	}
}
