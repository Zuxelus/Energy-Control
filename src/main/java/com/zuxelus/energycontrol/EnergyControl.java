package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.containers.slots.SlotHandler;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.proxy.IProxy;
import com.zuxelus.energycontrol.recipes.RecipesNew;
import com.zuxelus.energycontrol.tileentities.ScreenManager;
import com.zuxelus.energycontrol.websockets.SocketClient;

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
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

@Mod(modid = EnergyControl.MODID, dependencies="after:ic2;after:techreborn", acceptedMinecraftVersions = "[1.12.2]")
public class EnergyControl {
	public static final String MODID = "energycontrol";

	@SidedProxy(clientSide = "com.zuxelus.energycontrol.proxy.ClientProxy", serverSide = "com.zuxelus.energycontrol.proxy.ServerProxy")
	public static IProxy proxy;
	
	@Instance(MODID)
	public static EnergyControl instance;

	public static EnCtrlTab creativeTab = new EnCtrlTab();

	public static Logger logger;
	public static Map<String, OreHelper> oreHelper;
	
	public ScreenManager screenManager = new ScreenManager();
	
	@SideOnly(Side.CLIENT)
	public List<String> availableAlarms; //on client
	@SideOnly(Side.CLIENT)
	public List<String> serverAllowedAlarms; // will be loaded from server

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		proxy.loadConfig(event);
		proxy.importSound(event.getModConfigurationDirectory());
		//proxy.registerModelLoader();

		ChannelHandler.init();
		CrossModLoader.preInit();
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
		RecipesNew.addRecipes();
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		OreHelper.initList(event.getServer().worlds);
		if (EnergyControlConfig.wsEnabled && !EnergyControlConfig.wsHost.isEmpty())
			SocketClient.connect(EnergyControlConfig.wsHost, EnergyControlConfig.wsPort);
	}

	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent event) {
		if (EnergyControlConfig.wsEnabled)
			SocketClient.close();
	}
}
