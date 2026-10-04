package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.zlib.network.PacketBase;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

public record PacketKeys(boolean altPressed) implements PacketBase {
	public static final Id<PacketKeys> ID = new Id<>(Identifier.of(EnergyControl.MODID, "c2s_keys"));
	public static final PacketCodec<RegistryByteBuf, PacketKeys> CODEC = PacketCodecs.BOOL.<RegistryByteBuf>cast().xmap(PacketKeys::new, PacketKeys::altPressed);

	@Override
	public Id<PacketKeys> getId() {
		return ID;
	}

	public static void handle(PacketKeys packet, ServerPlayNetworking.Context context) {
		context.server().execute(() -> {
			EnergyControl.altPressed.put(context.player(), packet.altPressed());
		});
	}
}
