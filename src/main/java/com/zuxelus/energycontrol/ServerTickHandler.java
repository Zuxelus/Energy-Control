package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.network.PacketAlarm;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//@EventBusSubscriber(modid = EnergyControl.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class ServerTickHandler {
	public final static ServerTickHandler instance = new ServerTickHandler();

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		EnergyControl.INSTANCE.screenManager.clearWorld(event.getWorld());
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		if (!(event.getPlayer() instanceof ServerPlayerEntity))
			return;
		EnergyControl.altPressed.put(event.getPlayer(), false);
		NetworkHelper.sendToPlayer((ServerPlayerEntity) event.getPlayer(), new PacketAlarm(ConfigHandler.MAX_ALARM_RANGE.get(), ConfigHandler.ALLOWED_ALARMS.get()));
		/*if (EnergyControl.oreHelper != null)
			NetworkHelper.network.sendTo(new PacketOreHelper(EnergyControl.oreHelper), (EntityPlayerMP) event.player);*/
	}
}
