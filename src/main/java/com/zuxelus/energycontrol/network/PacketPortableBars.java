package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
import com.zuxelus.zlib.network.PacketBase;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

public record PacketPortableBars(boolean showBars) implements PacketBase {
	public static final Id<PacketPortableBars> ID = new Id<>(Identifier.of(EnergyControl.MODID, "c2s_portable_bars"));
	public static final PacketCodec<RegistryByteBuf, PacketPortableBars> CODEC = PacketCodecs.BOOL.<RegistryByteBuf>cast().xmap(PacketPortableBars::new, PacketPortableBars::showBars);

	@Override
	public Id<PacketPortableBars> getId() {
		return ID;
	}

	public static void handle(PacketPortableBars packet, ServerPlayNetworking.Context context) {
		context.server().execute(() -> {
			if (context.player().currentScreenHandler instanceof ContainerPortablePanel container)
				container.setShowBars(packet.showBars());
		});
	}
}
