package com.zuxelus.energycontrol;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.recipes.RecipesNew;
import com.zuxelus.energycontrol.tileentities.ScreenManager;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(name = EnergyControl.NAME, modid = EnergyControl.MODID, version = EnergyControl.VERSION, dependencies = "required-after:IC2", guiFactory = "com.zuxelus.energycontrol.config.GuiFactory", acceptedMinecraftVersions = "[1.7.10]")
public class EnergyControl {
	public static final String NAME = "Energy Control";
	public static final String MODID = "energycontrol";
	public static final String VERSION = "@VERSION@";

	@SidedProxy(clientSide = "com.zuxelus.energycontrol.ClientProxy", serverSide = "com.zuxelus.energycontrol.ServerProxy")
	public static ServerProxy proxy;
	@Instance(MODID)
	public static EnergyControl instance;

	// Mod's creative tab
	public static EnCtrlTab creativeTab = new EnCtrlTab();

	// For logging purposes
	public static Logger logger;
	public static ConfigHandler config;
	
	public int modelId;
	public ScreenManager screenManager = new ScreenManager();
	
	@SideOnly(Side.CLIENT)
	public List<String> availableAlarms; //on client
	@SideOnly(Side.CLIENT)
	public List<String> serverAllowedAlarms; // will be loaded from server

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		// Loads configuration
		proxy.loadConfig(event);
		proxy.importSound();

		// registers channel handler
		ChannelHandler.init();

		CrossModLoader.preinit();
		ItemHelper.onBlockRegistry();
		ItemHelper.onItemRegistry();
		ItemHelper.registerTileEntities();
	}

	@EventHandler
	public static void init(FMLInitializationEvent event) {
		// Register event handlers
		proxy.registerEventHandlers();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

		proxy.registerSpecialRenderers();
		CrossModLoader.init();
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		RecipesNew.addRecipes();
		CrossModLoader.postinit();
	}
}
