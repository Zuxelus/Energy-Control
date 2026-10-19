package com.zuxelus.energycontrol.tileentities;

import net.minecraft.nbt.CompoundTag;

public interface ITilePacketHandler {

	void onServerMessageReceived(CompoundTag tag);

	void onClientMessageReceived(CompoundTag tag);
}
