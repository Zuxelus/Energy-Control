package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketKeys implements IMessage, IMessageHandler<PacketKeys, IMessage> {
	private boolean altPressed;

	public PacketKeys() { }

	public PacketKeys(boolean altPressed) {
		this.altPressed = altPressed;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		altPressed = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(altPressed);
	}

	@Override
	public IMessage onMessage(PacketKeys message, MessageContext ctx) {
		EnergyControl.altPressed.put(ctx.getServerHandler().playerEntity, message.altPressed);
		return null;
	}
}
