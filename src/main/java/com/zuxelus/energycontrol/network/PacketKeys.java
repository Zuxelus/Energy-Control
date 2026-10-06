package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.zlib.network.PacketBase;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record PacketKeys(boolean altPressed) implements PacketBase {
	public static final Type<PacketKeys> ID = new Type<>(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "c2s_keys"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketKeys> CODEC = ByteBufCodecs.BOOL.<RegistryFriendlyByteBuf>cast().map(PacketKeys::new, PacketKeys::altPressed);

	@Override
	public Type<PacketKeys> type() {
		return ID;
	}

	public static void handle(PacketKeys packet, ServerPlayNetworking.Context context) {
		context.server().execute(() -> {
			EnergyControl.altPressed.put(context.player(), packet.altPressed());
		});
	}
}
