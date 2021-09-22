package com.zuxelus.energycontrol.network;

import java.util.function.Supplier;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;

public class PacketKeys {
	private boolean altPressed;

	public PacketKeys() { }

	public PacketKeys(boolean altPressed) {
		this.altPressed = altPressed;
	}

	public static PacketKeys decode(FriendlyByteBuf buf) {
		return new PacketKeys(buf.readBoolean());
	}

	public static void encode(PacketKeys pkt, FriendlyByteBuf buf) {
		buf.writeBoolean(pkt.altPressed);
	}

	public static void handle(PacketKeys message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			EnergyControl.altPressed.put(ctx.getSender(), message.altPressed);
		});
		ctx.setPacketHandled(true);
	}
}
