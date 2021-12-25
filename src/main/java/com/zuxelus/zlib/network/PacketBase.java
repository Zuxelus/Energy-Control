package com.zuxelus.zlib.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class PacketBase extends PacketByteBuf {

	public PacketBase() {
		super(Unpooled.buffer());
	}

	public abstract Identifier getId();
}
