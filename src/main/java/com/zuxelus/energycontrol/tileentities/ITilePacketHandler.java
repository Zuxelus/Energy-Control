package com.zuxelus.energycontrol.tileentities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public interface ITilePacketHandler {

	void onServerMessageReceived(CompoundTag tag);

	void onClientMessageReceived(CompoundTag tag);

	void onDataPacket(ClientboundBlockEntityDataPacket pkt);
}
