package com.zuxelus.energycontrol.network;

import java.util.ArrayList;
import java.util.Arrays;

import com.zuxelus.energycontrol.EnergyControl;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record PacketAlarm(int maxAlarmRange, String alarms) implements CustomPacketPayload {
	public static final Type<PacketAlarm> ID = new Type<>(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "s2c_alarm"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketAlarm> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, PacketAlarm::maxAlarmRange,
			ByteBufCodecs.STRING_UTF8, PacketAlarm::alarms,
			PacketAlarm::new);

	@Override
	public Type<PacketAlarm> type() {
		return ID;
	}

	public static void handleClient(PacketAlarm packet, ClientPlayNetworking.Context context) {
		context.client().execute(() -> {
			EnergyControl.INSTANCE.serverMaxAlarmRange = packet.maxAlarmRange();
			EnergyControl.INSTANCE.serverAllowedAlarms = new ArrayList<String>(Arrays.asList(packet.alarms().split(",")));
		});
	}
}
