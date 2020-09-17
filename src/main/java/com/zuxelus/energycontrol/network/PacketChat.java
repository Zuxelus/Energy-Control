package com.zuxelus.energycontrol.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;

public class PacketChat implements IMessage, IMessageHandler<PacketChat, IMessage> {
	private String message;
	private int type;
	private int value;

	public PacketChat() { }

	public PacketChat(String message, int type, int value) {
		this.message = message;
		this.type = type;
		this.value = value;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		message = ByteBufUtils.readUTF8String(buf);
		type = buf.readInt();
		value = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, message);
		buf.writeInt(type);
		buf.writeInt(value);
	}

	@Override
	public IMessage onMessage(PacketChat messages, MessageContext ctx) {
		switch (messages.type) {
		case 1:
			String[] modeName = { I18n.format("info.normal"), I18n.format("info.rapidfire"), I18n.format("info.spread"),
					I18n.format("info.sniper"), I18n.format("info.flame"), I18n.format("info.explosive") };
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(I18n.format(messages.message, modeName[messages.value])));
			break;
		default:
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(messages.message));
			break;
		}
		return null;
	}
}
