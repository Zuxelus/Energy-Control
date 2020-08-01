package com.zuxelus.energycontrol.blockentities;

import com.zuxelus.energycontrol.network.BlockEntityCustomUpdatePacket;

import net.minecraft.nbt.CompoundTag;

public interface ITilePacketHandler {

	void onServerMessageReceived(CompoundTag tag);

	void onClientMessageReceived(CompoundTag tag);
	
	void onDataPacket(BlockEntityCustomUpdatePacket pkt);
}
