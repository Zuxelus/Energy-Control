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
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.ScreenManager;
import com.zuxelus.energycontrol.utils.SoundLoader;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EnergyControl.MODID)
public class EnergyControl {
	public static final String NAME = "Energy Control";
	public static final String MODID = "energycontrol";
	public static final String VERSION = "@VERSION@";

	public static EnergyControl INSTANCE;
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public ScreenManager screenManager = new ScreenManager();

	@OnlyIn(Dist.CLIENT)
	public List<String> availableAlarms; //on client
	@OnlyIn(Dist.CLIENT)
	public List<String> serverAllowedAlarms; // will be loaded from server
	public static Map<Player, Boolean> altPressed = new HashMap<Player, Boolean>();

	public EnergyControl() {
		INSTANCE = this;
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(SideProxy::commonSetup);
		modEventBus.addListener(SoundLoader::locatePacks);
		ModItems.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ECCreativeTab.CREATIVE_TABS.register(modEventBus);
		ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
		ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
		ModItems.RECIPE_SERIALIZERS.register(modEventBus);
		MinecraftForge.EVENT_BUS.register(ServerTickHandler.instance);
		ModLoadingContext.get().registerConfig(Type.COMMON, ConfigHandler.COMMON_CONFIG, "energycontrol-common.toml");
		CrossModLoader.init();
	}
}
