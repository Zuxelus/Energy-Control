package com.zuxelus.energycontrol.network;

import java.util.Arrays;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.EnergyControlConfig;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAlarm implements IMessage, IMessageHandler<PacketAlarm, IMessage> {
	private int maxAlarmRange;
	private String[] allowedAlarms;

	public PacketAlarm() { }

	public PacketAlarm(int range, String[] alarms) {
		maxAlarmRange = range;
		allowedAlarms = alarms;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		maxAlarmRange = buf.readInt();
		allowedAlarms = ByteBufUtils.readUTF8String(buf).split(",");
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(maxAlarmRange);
		ByteBufUtils.writeUTF8String(buf, String.join(",", allowedAlarms));
	}

	@Override
	public IMessage onMessage(PacketAlarm message, MessageContext ctx) {
		EnergyControlConfig.maxAlarmRange = message.maxAlarmRange;
		EnergyControl.instance.serverAllowedAlarms = Arrays.asList(message.allowedAlarms);
		return null;
	}
}
