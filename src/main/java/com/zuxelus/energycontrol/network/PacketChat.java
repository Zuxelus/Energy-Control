package com.zuxelus.energycontrol.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketChat implements IMessage,IMessageHandler<PacketChat, IMessage> {
	private String message;

	public PacketChat() {}

	public PacketChat(String message) {
		this.message = message;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		message = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, message);
	}

	@Override
	public IMessage onMessage(PacketChat messages, MessageContext ctx) {
		Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentTranslation(messages.message), false);
		return null;
	}
}
