package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.ContainerTimer;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.network.PacketAlarm;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
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
		if (!(event.getPlayer() instanceof ServerPlayer))
			return;
		EnergyControl.altPressed.put(event.getPlayer(), false);
		NetworkHelper.sendToPlayer((ServerPlayer) event.getPlayer(), new PacketAlarm(ConfigHandler.MAX_ALARM_RANGE.get(), ConfigHandler.ALLOWED_ALARMS.get()));
		/*if (EnergyControl.oreHelper != null)
			NetworkHelper.network.sendTo(new PacketOreHelper(EnergyControl.oreHelper), (EntityPlayerMP) event.player);*/
	}

	@SubscribeEvent
	public void onPlayerContainerOpen(PlayerContainerEvent.Open event) {
		if (!(event.getPlayer() instanceof ServerPlayer))
			return;
		AbstractContainerMenu container = event.getContainer();
		if (container instanceof ContainerBase)
			((ContainerBase) container).listeners.add(event.getPlayer());
		if (container instanceof ContainerTimer)
			((ContainerTimer) container).listeners.add((ServerPlayer) event.getPlayer());
	}
}
