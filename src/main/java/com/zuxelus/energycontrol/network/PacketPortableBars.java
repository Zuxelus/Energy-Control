package com.zuxelus.energycontrol.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerPortablePanel;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

// the portable panel GUI switched its progress bars on or off; the setting is saved in the panel stack
public record PacketPortableBars(boolean showBars) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PacketPortableBars> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "portable_bars"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketPortableBars> STREAM_CODEC = ByteBufCodecs.BOOL.<RegistryFriendlyByteBuf>cast().map(PacketPortableBars::new, PacketPortableBars::showBars);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	// server, main thread
	public static void handle(PacketPortableBars message, IPayloadContext ctx) {
		if (ctx.player().containerMenu instanceof ContainerPortablePanel container)
			container.setShowBars(message.showBars());
	}
}
