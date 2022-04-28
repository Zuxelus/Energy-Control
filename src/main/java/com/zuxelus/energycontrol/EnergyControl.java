package com.zuxelus.energycontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.slots.SlotHandler;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.proxy.IProxy;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.tileentities.ScreenManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = EnergyControl.MODID, dependencies="after:IC2;after:techreborn", guiFactory = "com.zuxelus.energycontrol.config.GuiFactory", acceptedMinecraftVersions = "[1.10.2]")
public class EnergyControl {
	public static final String MODID = "energycontrol";

	@SidedProxy(clientSide = "com.zuxelus.energycontrol.proxy.ClientProxy", serverSide = "com.zuxelus.energycontrol.proxy.ServerProxy")
	public static IProxy proxy;

	@Instance(MODID)
	public static EnergyControl instance;

	public static EnCtrlTab creativeTab = new EnCtrlTab();

	public static Logger logger;
	public static ConfigHandler config;
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
		//proxy.registerModelLoader();

		ChannelHandler.init();
		CrossModLoader.preInit();
		proxy.registerItems(); // 1.10.2 (because onItemRegistry before FMLPreInitializationEvent)
		ModItems.registerTileEntities();
		MinecraftForge.EVENT_BUS.register(new SlotHandler());
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
		Recipes.addRecipes();
	}

	/*@EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		if (EnergyControl.config.wsEnabled && !EnergyControl.config.wsHost.isEmpty())
			SocketClient.connect(EnergyControl.config.wsHost, EnergyControl.config.wsPort);
	}

	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent event) {
		if (EnergyControl.config.wsEnabled)
			SocketClient.close();
	}*/
}
