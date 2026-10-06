package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
import com.zuxelus.zlib.network.PacketBase;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record PacketPortableBars(boolean showBars) implements PacketBase {
	public static final Type<PacketPortableBars> ID = new Type<>(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "c2s_portable_bars"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketPortableBars> CODEC = ByteBufCodecs.BOOL.<RegistryFriendlyByteBuf>cast().map(PacketPortableBars::new, PacketPortableBars::showBars);

	@Override
	public Type<PacketPortableBars> type() {
		return ID;
	}

	public static void handle(PacketPortableBars packet, ServerPlayNetworking.Context context) {
		context.server().execute(() -> {
			if (context.player().containerMenu instanceof ContainerPortablePanel container)
				container.setShowBars(packet.showBars());
		});
	}
}
