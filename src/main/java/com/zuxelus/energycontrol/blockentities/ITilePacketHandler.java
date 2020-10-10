package com.zuxelus.energycontrol.blockentities;

import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;

public interface ITilePacketHandler {

	void onServerMessageReceived(CompoundTag tag);

	void onClientMessageReceived(CompoundTag tag);

	void onDataPacket(BlockEntityUpdateS2CPacket pkt);
}
