package com.zuxelus.energycontrol.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
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
		/*String[] chunks = messages.message.split(":");
		messages.message = I18n.format("msg.ec." + chunks[0]);
		if (chunks.length > 1) {
			List<String> list = new ArrayList<String>(Arrays.asList(chunks));
			list.remove(0);
			chunks = list.toArray(chunks);
			messages.message = String.format(messages.message,(Object[]) chunks);
		}*/
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new TextComponentTranslation(messages.message));
		return null;
	}
}
