package com.zuxelus.energycontrol.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

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
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(messages.message));
		return null;
	}
}
