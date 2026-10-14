package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketKeys(boolean altPressed) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketKeys> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "keys"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketKeys> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, PacketKeys::altPressed,
			PacketKeys::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	// server
	public static void handle(PacketKeys message, IPayloadContext ctx) {
		EnergyControl.altPressed.put(ctx.player(), message.altPressed);
	}
}
