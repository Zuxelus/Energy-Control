package com.zuxelus.energycontrol.network;

import java.util.function.Supplier;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketKeys {
	private boolean altPressed;

	public PacketKeys() { }

	public PacketKeys(boolean altPressed) {
		this.altPressed = altPressed;
	}

	public static PacketKeys decode(PacketBuffer buf) {
		return new PacketKeys(buf.readBoolean());
	}

	public static void encode(PacketKeys pkt, PacketBuffer buf) {
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
