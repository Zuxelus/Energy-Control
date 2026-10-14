package com.zuxelus.energycontrol.websockets;

import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SocketClientHandler implements WebSocket.Listener {

	@Override
	public void onOpen(WebSocket webSocket) {
		System.out.println("WebSocket Client connected!");
		webSocket.request(1);
	}

	@Override
	public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
		System.out.println("WebSocket Client received message: " + data);
		webSocket.request(1);
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletionStage<?> onPong(WebSocket webSocket, java.nio.ByteBuffer message) {
		System.out.println("WebSocket Client received pong");
		webSocket.request(1);
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
		System.out.println("WebSocket Client received closing");
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public void onError(WebSocket webSocket, Throwable error) {
		error.printStackTrace();
	}
}
