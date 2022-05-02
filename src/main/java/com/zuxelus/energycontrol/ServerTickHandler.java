package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.network.NetworkHelper;
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
		if (!(event.player instanceof EntityPlayerMP))
			return;
		EnergyControl.altPressed.put(event.player, false);
		NetworkHelper.network.sendTo(new PacketAlarm(EnergyControl.config.howlerAlarmRange, EnergyControl.config.allowedAlarms.split(",")), (EntityPlayerMP) event.player);
	}
}
