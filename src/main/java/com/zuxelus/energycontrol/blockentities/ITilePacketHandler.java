package com.zuxelus.energycontrol.blockentities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

public interface ITilePacketHandler {

	void onServerMessageReceived(CompoundTag tag);

	void onClientMessageReceived(CompoundTag tag);

	void onDataPacket(BlockEntityUpdateS2CPacket pkt);
}
