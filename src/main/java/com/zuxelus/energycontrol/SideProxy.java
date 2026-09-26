package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.network.ChannelHandler;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class SideProxy {

	public static void commonSetup(FMLCommonSetupEvent event) {
		ChannelHandler.init();
	}
}
