package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.zlib.network.PacketBase;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PacketKeys extends PacketBase {
	public static final Identifier ID = new Identifier(EnergyControl.MODID, "c2s_keys");

	public PacketKeys(boolean altPressed) {
		writeBoolean(altPressed);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		boolean altPressed = buf.readBoolean();
		server.execute(() -> {
			EnergyControl.altPressed.put(player, altPressed);
		});
	}
}
