package com.zuxelus.energycontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModCapabilities;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.tileentities.ScreenManager;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(EnergyControl.MODID)
public class EnergyControl {
	public static final String NAME = "Energy Control";
	public static final String MODID = "energycontrol";
	public static final String VERSION = "1";

	public static EnergyControl INSTANCE;
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public ScreenManager screenManager = new ScreenManager();

	public List<String> availableAlarms; //on client
	public List<String> serverAllowedAlarms; // will be loaded from server
	public static Map<Player, Boolean> altPressed = new HashMap<Player, Boolean>();

	public EnergyControl(IEventBus modEventBus, ModContainer container) {
		INSTANCE = this;
		modEventBus.addListener(ChannelHandler::register);
		modEventBus.addListener(ModCapabilities::register);
		modEventBus.addListener(CrossModLoader::onRegister);
		ModItems.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModItems.RECIPE_TYPES.register(modEventBus);
		ModItems.RECIPE_SERIALIZERS.register(modEventBus);
		ECCreativeTab.CREATIVE_TABS.register(modEventBus);
		ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
		ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
		NeoForge.EVENT_BUS.register(ServerTickHandler.instance);
		container.registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_CONFIG, "energycontrol-common.toml");
		CrossModLoader.init();
	}
}
