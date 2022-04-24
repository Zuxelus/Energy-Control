package com.zuxelus.energycontrol.websockets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import com.zuxelus.energycontrol.EnergyControlConfig;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

public class SocketClient {
	private static EventLoopGroup group;
	private static Channel channel;

	public static void connect(String host, int port) {
		if (group == null)
			group = new NioEventLoopGroup();
		try {
			final SocketClientHandler handler = new SocketClientHandler(WebSocketClientHandshakerFactory.newHandshaker(
					new URI(String.format("ws://%s:%s/?token=%s&id=%s", host, port, EnergyControlConfig.wsToken, EnergyControlConfig.wsServerID)), WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

			Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192), /*WebSocketClientCompressionHandler.INSTANCE,*/ handler); // TODO
						}
					});

			channel = bootstrap.connect(host, port).sync().channel();
			handler.handshakeFuture().sync();
		} catch (Throwable t) {}
	}

	public static void sendMessage(String message) {
		if (channel == null)
			return;
		WebSocketFrame frame = new TextWebSocketFrame(message);
		channel.writeAndFlush(frame);
	}

	public static void close() {
		if (group == null)
			return;
		if (!group.isShuttingDown())
			group.shutdownGracefully();
	}
}
