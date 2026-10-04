package com.zuxelus.energycontrol.network;

import java.util.ArrayList;
import java.util.Arrays;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.zlib.network.PacketBase;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

public record PacketAlarm(String alarms) implements PacketBase {
	public static final Id<PacketAlarm> ID = new Id<>(Identifier.of(EnergyControl.MODID, "s2c_alarm"));
	public static final PacketCodec<RegistryByteBuf, PacketAlarm> CODEC = PacketCodecs.STRING.<RegistryByteBuf>cast().xmap(PacketAlarm::new, PacketAlarm::alarms);

	public PacketAlarm(int range, String alarms) {
		this(alarms);
	}

	@Override
	public Id<PacketAlarm> getId() {
		return ID;
	}

	public static void handleClient(PacketAlarm packet, ClientPlayNetworking.Context context) {
		context.client().execute(() -> {
			//ConfigHandler.MAX_ALARM_RANGE.set(buf.readVarInt());
			EnergyControl.INSTANCE.serverAllowedAlarms = new ArrayList<String>(Arrays.asList(packet.alarms().split(",")));
		});
	}
}
