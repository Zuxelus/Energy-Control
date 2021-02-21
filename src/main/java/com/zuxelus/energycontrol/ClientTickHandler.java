package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.network.ChannelHandler;

import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = EnergyControl.MODID, value = Dist.CLIENT)
public class ClientTickHandler {
	public final static ClientTickHandler instance = new ClientTickHandler();
	public static boolean altPressed;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END)
			return;
		boolean alt = Screen.hasAltDown();
		if (altPressed != alt) {
			altPressed = alt;
			ChannelHandler.updateSeverKeys(alt);
		}
	}
}
