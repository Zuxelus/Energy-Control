package com.zuxelus.energycontrol.tileentities;

import net.minecraft.nbt.NBTTagCompound;

public interface ITilePacketHandler {

	void onServerMessageReceived(NBTTagCompound tag);

	void onClientMessageReceived(NBTTagCompound tag);

}
