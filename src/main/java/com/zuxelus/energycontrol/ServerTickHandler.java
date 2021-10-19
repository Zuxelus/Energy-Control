package com.zuxelus.energycontrol;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.network.PacketAlarm;
import com.zuxelus.energycontrol.network.PacketOreHelper;
import com.zuxelus.energycontrol.websockets.SocketClient;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ServerTickHandler {
	public final static ServerTickHandler instance = new ServerTickHandler();
	public Map<String, JsonObject> cards = new HashMap<String, JsonObject>();
	public int updateTicker;

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

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if (event.phase == Phase.END && EnergyControlConfig.wsEnabled)
			if (updateTicker-- < 0) {
				updateTicker = EnergyControlConfig.wsRefreshRate - 1;
				if (!cards.isEmpty()) {
					JsonObject json = new JsonObject();
					json.addProperty("id", EnergyControlConfig.wsServerID);
					JsonArray array = new JsonArray();
					for (Map.Entry<String, JsonObject> card : cards.entrySet())
						array.add(card.getValue());
					json.add("cards", array);
					SocketClient.sendMessage(json.toString());
					cards.clear();
				}
			}
	}
}
