package com.zuxelus.energycontrol.network;

import java.util.ArrayList;
import java.util.Arrays;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketAlarm(int maxAlarmRange, String allowedAlarms) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketAlarm> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "alarm"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketAlarm> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, PacketAlarm::maxAlarmRange,
			ByteBufCodecs.STRING_UTF8, PacketAlarm::allowedAlarms,
			PacketAlarm::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	// client
	public static void handle(PacketAlarm message, IPayloadContext ctx) {
		EnergyControl.INSTANCE.serverAllowedAlarms = new ArrayList<String>(Arrays.asList(message.allowedAlarms.split(",")));
	}
}
