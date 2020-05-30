package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.network.PacketAlarm;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.WorldEvent;

public class ServerTickHandler {
	public final static ServerTickHandler instance = new ServerTickHandler();

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		EnergyControl.instance.screenManager.clearWorld(event.world);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		ChannelHandler.network.sendTo(
				new PacketAlarm(EnergyControl.config.maxAlarmRange, EnergyControl.config.allowedAlarms), (EntityPlayerMP) event.player);
	}
}
