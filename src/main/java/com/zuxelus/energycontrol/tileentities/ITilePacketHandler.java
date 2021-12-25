package com.zuxelus.energycontrol.tileentities;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

public interface ITilePacketHandler {

	void onServerMessageReceived(NbtCompound tag);

	void onClientMessageReceived(NbtCompound tag);

	void onDataPacket(BlockEntityUpdateS2CPacket pkt);
}
