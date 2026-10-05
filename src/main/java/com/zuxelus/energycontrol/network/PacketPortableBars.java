package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
import com.zuxelus.zlib.network.PacketBase;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PacketPortableBars extends PacketBase {
	public static final Identifier ID = new Identifier(EnergyControl.MODID, "c2s_portable_bars");

	public PacketPortableBars(boolean showBars) {
		writeBoolean(showBars);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		boolean showBars = buf.readBoolean();
		server.execute(() -> {
			if (player.currentScreenHandler instanceof ContainerPortablePanel container)
				container.setShowBars(showBars);
		});
	}
}
