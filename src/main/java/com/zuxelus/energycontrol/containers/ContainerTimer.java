package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerTimer extends Container {
	public TileEntityTimer te;
	private int lastTime;
	private boolean lastIsWorking;

	public ContainerTimer(EntityPlayer player, TileEntityTimer te) {
		this.te = te;
		lastTime = 0;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistanceSq(te.getPos().getX() + 0.5D, te.getPos().getY() + 0.5D, te.getPos().getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int time = te.getTime();
		boolean isWorking = te.getIsWorking();
		for (net.minecraft.inventory.IContainerListener listener : listeners) {
			if (lastTime != time)
				NetworkHelper.updateClientTileEntity(listener, te.getPos(), 1, time);
			if (lastIsWorking != isWorking)
				NetworkHelper.updateClientTileEntity(listener, te.getPos(), 2, isWorking ? 1 : 0);
		}
		lastTime = time;
		lastIsWorking = isWorking;
	}
}
