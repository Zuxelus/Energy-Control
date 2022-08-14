package com.zuxelus.energycontrol;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.ContainerTimer;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.network.PacketAlarm;
import com.zuxelus.energycontrol.websockets.SocketClient;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerTickHandler {
	public final static ServerTickHandler instance = new ServerTickHandler();
	public Map<String, JsonObject> cards = new HashMap<String, JsonObject>();
	public int updateTicker;

	@SubscribeEvent
	public void onWorldUnload(LevelEvent.Unload event) {
		EnergyControl.INSTANCE.screenManager.clearWorld(event.getLevel());
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer))
			return;
		EnergyControl.altPressed.put(event.getEntity(), false);
		NetworkHelper.sendToPlayer((ServerPlayer) event.getEntity(), new PacketAlarm(ConfigHandler.MAX_ALARM_RANGE.get(), ConfigHandler.ALLOWED_ALARMS.get()));
		/*if (EnergyControl.oreHelper != null)
			NetworkHelper.network.sendTo(new PacketOreHelper(EnergyControl.oreHelper), (EntityPlayerMP) event.player);*/
	}

	@SubscribeEvent
	public void onPlayerContainerOpen(PlayerContainerEvent.Open event) {
		if (!(event.getEntity() instanceof ServerPlayer))
			return;
		AbstractContainerMenu container = event.getContainer();
		if (container instanceof ContainerBase)
			((ContainerBase) container).listeners.add(event.getEntity());
		if (container instanceof ContainerTimer)
			((ContainerTimer) container).listeners.add((ServerPlayer) event.getEntity());
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if (event.phase == Phase.END && ConfigHandler.WS_ENABLED.get())
			if (updateTicker-- < 0) {
				updateTicker = ConfigHandler.WS_REFRESH_RATE.get() - 1;
				if (!cards.isEmpty()) {
					JsonObject json = new JsonObject();
					json.addProperty("id", ConfigHandler.WS_SERVER_ID.get());
					JsonArray array = new JsonArray();
					for (Map.Entry<String, JsonObject> card : cards.entrySet())
						array.add(card.getValue());
					json.add("cards", array);
					SocketClient.sendMessage(json.toString());
					cards.clear();
				}
			}
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		if (ConfigHandler.WS_ENABLED.get() && !ConfigHandler.WS_HOST.get().isEmpty())
			SocketClient.connect(ConfigHandler.WS_HOST.get(), ConfigHandler.WS_PORT.get());
	}

	@SubscribeEvent
	public void onServerStopping(ServerStoppingEvent event) {
		if (ConfigHandler.WS_ENABLED.get())
			SocketClient.close();
	}
}
