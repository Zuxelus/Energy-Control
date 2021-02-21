package com.zuxelus.energycontrol.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketAlarm {
	private int maxAlarmRange;
	private String allowedAlarms;

	public PacketAlarm() { }

	public PacketAlarm(int range, String alarms) {
		maxAlarmRange = range;
		allowedAlarms = alarms;
	}

	public static PacketAlarm decode(PacketBuffer buf) {
		int maxAlarmRange = buf.readInt();
		String allowedAlarms = buf.readString();
		return new PacketAlarm(maxAlarmRange, allowedAlarms);
	}

	public static void encode(PacketAlarm pkt, PacketBuffer buf) {
		buf.writeInt(pkt.maxAlarmRange);
		buf.writeString(pkt.allowedAlarms);
	}

	public static void handle(PacketAlarm message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			//ConfigHandler.MAX_ALARM_RANGE.set(message.maxAlarmRange);
			EnergyControl.INSTANCE.serverAllowedAlarms = new ArrayList<String>(Arrays.asList(message.allowedAlarms.split(",")));
		});
		ctx.setPacketHandled(true);
	}
}
