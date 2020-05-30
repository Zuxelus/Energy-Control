package com.zuxelus.energycontrol.network;

import java.util.ArrayList;
import java.util.Arrays;

import com.zuxelus.energycontrol.EnergyControl;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketAlarm implements IMessage, IMessageHandler<PacketAlarm, IMessage> {
	private int maxAlarmRange;
	private String allowedAlarms;

	public PacketAlarm() { }

	public PacketAlarm(int range, String alarms) {
		maxAlarmRange = range;
		allowedAlarms = alarms;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		maxAlarmRange = buf.readInt();
		allowedAlarms = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(maxAlarmRange);
		ByteBufUtils.writeUTF8String(buf, allowedAlarms);
	}

	@Override
	public IMessage onMessage(PacketAlarm message, MessageContext ctx) {
		EnergyControl.config.maxAlarmRange = message.maxAlarmRange;
		EnergyControl.instance.serverAllowedAlarms = new ArrayList<String>(Arrays.asList(message.allowedAlarms.split(",")));
		return null;
	}
}
