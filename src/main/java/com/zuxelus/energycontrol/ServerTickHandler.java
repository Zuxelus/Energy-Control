package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.network.NetworkHelper;
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
		if (!(event.player instanceof EntityPlayerMP))
			return;
		NetworkHelper.network.sendTo(new PacketAlarm(EnergyControlConfig.howlerAlarmRange, EnergyControlConfig.allowedAlarms), (EntityPlayerMP) event.player);
		if (EnergyControl.oreHelper != null)
			NetworkHelper.network.sendTo(new PacketOreHelper(EnergyControl.oreHelper), (EntityPlayerMP) event.player);
	}
}
