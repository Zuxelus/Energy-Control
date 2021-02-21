package com.zuxelus.energycontrol.tileentities;

import net.minecraft.nbt.CompoundNBT;

public interface ITilePacketHandler {

	void onServerMessageReceived(CompoundNBT tag);

	void onClientMessageReceived(CompoundNBT tag);
}
