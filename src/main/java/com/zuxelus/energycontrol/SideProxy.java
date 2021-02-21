package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.network.ChannelHandler;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class SideProxy {
	SideProxy() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_CONFIG);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(SideProxy::commonSetup);
	}

	public static void commonSetup(FMLCommonSetupEvent event) { 
		ChannelHandler.init();
	}

	static class Client extends SideProxy {
		Client() {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::clientSetup);
		}

		private static void clientSetup(FMLClientSetupEvent event) { }

	}

	static class Server extends SideProxy {
		Server() {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(Server::serverSetup);
		}

		private static void serverSetup(FMLDedicatedServerSetupEvent event) { }
	}
}
