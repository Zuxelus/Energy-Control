package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;

public class ContainerTimer extends Container {
	private TileEntityTimer te;
	private int lastTime;
	private boolean lastIsWorking;

	public ContainerTimer(TileEntityTimer te) {
		this.te = te;
		lastTime = 0;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistanceSq(te.xCoord + 0.5D, te.yCoord + 0.5D, te.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int time = te.getTime();
		boolean isWorking = te.getIsWorking();
		for (int i = 0; i < crafters.size(); i++) {
			if (lastTime != time)
				NetworkHelper.updateClientTileEntity((ICrafting)crafters.get(i), te.xCoord, te.yCoord, te.zCoord, 1, time);
			if (lastIsWorking != isWorking)
				NetworkHelper.updateClientTileEntity((ICrafting)crafters.get(i), te.xCoord, te.yCoord, te.zCoord, 2, isWorking ? 1 : 0);
		}
		lastTime = time;
		lastIsWorking = isWorking;
	}
}
