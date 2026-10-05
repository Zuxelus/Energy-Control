package com.zuxelus.energycontrol.network;

import java.util.ArrayList;
import java.util.Arrays;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.zlib.network.PacketBase;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class PacketAlarm extends PacketBase {
	public static final Identifier ID = new Identifier(EnergyControl.MODID, "s2c_alarm");

	public PacketAlarm() { }

	public PacketAlarm(int range, String alarms) {
		//writeVarInt(range);
		writeString(alarms);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void handleClient(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		String alarms = buf.readString();
		client.execute(() -> {
			//ConfigHandler.MAX_ALARM_RANGE.set(buf.readVarInt());
			EnergyControl.INSTANCE.serverAllowedAlarms = new ArrayList<String>(Arrays.asList(alarms.split(",")));
		});
	}
}
