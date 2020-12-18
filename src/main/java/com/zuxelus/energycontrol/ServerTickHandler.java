package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.network.PacketAlarm;
import com.zuxelus.energycontrol.network.PacketOreHelper;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class ServerTickHandler {
	public final static ServerTickHandler instance = new ServerTickHandler();

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		EnergyControl.instance.screenManager.clearWorld(event.getWorld());
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		ChannelHandler.network.sendTo(new PacketAlarm(EnergyControl.config.maxAlarmRange, EnergyControl.config.allowedAlarms), (EntityPlayerMP) event.player);
		if (EnergyControl.oreHelper != null)
			ChannelHandler.network.sendTo(new PacketOreHelper(EnergyControl.oreHelper), (EntityPlayerMP) event.player);
	}
}
