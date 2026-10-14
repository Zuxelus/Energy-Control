package com.zuxelus.energycontrol.websockets;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;

import com.zuxelus.energycontrol.config.ConfigHandler;

// uses the websocket client of the JDK, netty-codec-http is not on the 1.21.1 classpath
public class SocketClient {
	private static volatile WebSocket socket;

	public static void connect(String host, int port) {
		try {
			URI uri = new URI(String.format("ws://%s:%s/?token=%s&id=%s", host, port, ConfigHandler.WS_TOKEN.get(), ConfigHandler.WS_SERVER_ID.get()));
			HttpClient.newHttpClient().newWebSocketBuilder().connectTimeout(Duration.ofSeconds(10))
					.buildAsync(uri, new SocketClientHandler()).whenComplete((webSocket, error) -> {
						if (error == null)
							socket = webSocket;
					});
		} catch (Throwable t) {}
	}

	public static void sendMessage(String message) {
		WebSocket webSocket = socket;
		if (webSocket == null || webSocket.isOutputClosed())
			return;
		webSocket.sendText(message, true);
	}

	public static void close() {
		WebSocket webSocket = socket;
		socket = null;
		if (webSocket != null && !webSocket.isOutputClosed())
			webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "");
	}
}
