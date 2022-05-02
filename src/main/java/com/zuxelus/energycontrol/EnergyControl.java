package com.zuxelus.energycontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.proxy.IProxy;
import com.zuxelus.energycontrol.recipes.Recipes;
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
import net.minecraft.entity.player.EntityPlayer;

@Mod(name = EnergyControl.NAME, modid = EnergyControl.MODID, version = EnergyControl.VERSION, dependencies = "required-after:IC2", guiFactory = "com.zuxelus.energycontrol.config.GuiFactory", acceptedMinecraftVersions = "[1.7.10]")
public class EnergyControl {
	public static final String NAME = "Energy Control";
	public static final String MODID = "energycontrol";
	public static final String VERSION = "@VERSION@";

	@SidedProxy(clientSide = "com.zuxelus.energycontrol.proxy.ClientProxy", serverSide = "com.zuxelus.energycontrol.proxy.ServerProxy")
	public static IProxy proxy;

	@Instance(MODID)
	public static EnergyControl instance;

	public static EnCtrlTab creativeTab = new EnCtrlTab();

	public static Logger logger;
	public static ConfigHandler config;
	
	public int modelId;
	public ScreenManager screenManager = new ScreenManager();

	@SideOnly(Side.CLIENT)
	public List<String> availableAlarms; //on client
	@SideOnly(Side.CLIENT)
	public List<String> serverAllowedAlarms; // will be loaded from server
	public static Map<EntityPlayer, Boolean> altPressed = new HashMap<EntityPlayer, Boolean>();	

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		proxy.loadConfig(event);
		proxy.importSound(event.getModConfigurationDirectory());

		ChannelHandler.init();
		CrossModLoader.preInit();
		ModItems.onBlockRegistry();
		ModItems.onItemRegistry();
		ModItems.registerTileEntities();
	}

	@EventHandler
	public static void init(FMLInitializationEvent event) {
		proxy.registerEventHandlers();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

		proxy.registerSpecialRenderers();
		CrossModLoader.init();
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		Recipes.addRecipes();
	}
}
