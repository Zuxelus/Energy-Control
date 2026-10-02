package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipeType;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;

@EventBusSubscriber(modid = EnergyControl.MODID, value = Dist.CLIENT)
public class ClientTickHandler {
	public static boolean altPressed;

	@SubscribeEvent
	public static void onRecipesReceived(RecipesUpdatedEvent event) {
			KitAssemblerRecipeType.setClientRecipes(event.getRecipeManager());
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		boolean alt = net.minecraft.client.gui.screens.Screen.hasAltDown();
		if (altPressed != alt) {
			altPressed = alt;
			if (mc.getConnection() != null)
				ChannelHandler.updateSeverKeys(alt);
		}
	}
}
