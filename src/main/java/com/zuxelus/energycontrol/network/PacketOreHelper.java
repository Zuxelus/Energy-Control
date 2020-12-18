package com.zuxelus.energycontrol.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.OreHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOreHelper implements IMessage, IMessageHandler<PacketOreHelper, IMessage> {
	private Map<String, OreHelper> helper;

	public PacketOreHelper() { }

	public PacketOreHelper(Map<String, OreHelper> helper) {
		this.helper = helper;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		helper = new HashMap<String, OreHelper>();
		int size = buf.readInt();
		for (int i = 0; i < size; i++) {
			String name = ByteBufUtils.readUTF8String(buf);
			helper.put(name, new OreHelper(buf));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(helper.size());
		for (Map.Entry<String, OreHelper> entry : helper.entrySet()) {
			ByteBufUtils.writeUTF8String(buf, entry.getKey());
			entry.getValue().writeToBuf(buf);
		}
	}

	@Override
	public IMessage onMessage(PacketOreHelper message, MessageContext ctx) {
		EnergyControl.oreHelper = message.helper;
		return null;
	}
}
