package com.zuxelus.energycontrol;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.websockets.SocketClient;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ServerTickHandler {
	public final static ServerTickHandler instance = new ServerTickHandler();
	public Map<String, JsonObject> cards = new HashMap<String, JsonObject>();
	public int updateTicker;

	public static void init() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> instance.onServerStarting());
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> instance.onServerStopping());
		ServerTickEvents.END_SERVER_TICK.register(server -> instance.onServerTick());
	}

	private void onServerTick() {
		if (ConfigHandler.wsEnabled)
			if (updateTicker-- < 0) {
				updateTicker = ConfigHandler.wsRefreshRate - 1;
				if (!cards.isEmpty()) {
					JsonObject json = new JsonObject();
					json.addProperty("id", ConfigHandler.wsServerId);
					JsonArray array = new JsonArray();
					for (Map.Entry<String, JsonObject> card : cards.entrySet())
						array.add(card.getValue());
					json.add("cards", array);
					SocketClient.sendMessage(json.toString());
					cards.clear();
				}
			}
	}

	private void onServerStarting() {
		if (ConfigHandler.wsEnabled && !ConfigHandler.wsHost.isEmpty())
			SocketClient.connect(ConfigHandler.wsHost, ConfigHandler.wsPort);
	}

	private void onServerStopping() {
		if (ConfigHandler.wsEnabled)
			SocketClient.close();
	}
}
