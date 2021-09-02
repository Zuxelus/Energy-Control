package com.zuxelus.energycontrol.containers;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;

public class ContainerTimer extends Container {
	public TileEntityTimer te;
	private int lastTime;
	private boolean lastIsWorking;
	private final List<IContainerListener> listeners = Lists.newArrayList();

	public ContainerTimer(int windowId, PlayerInventory inventory, PacketBuffer data) {
		this(windowId, inventory, (TileEntityTimer) ContainerBase.getTileEntity(inventory, data));
	}

	public ContainerTimer(int windowId, PlayerInventory inventory, TileEntityTimer te) {
		super(ModContainerTypes.timer.get(), windowId);
		this.te = te;
		lastTime = 0;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return player.getDistanceSq(te.getPos().getX() + 0.5D, te.getPos().getY() + 0.5D, te.getPos().getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		listeners.add(listener);
	}

	@Override
	public void removeListener(IContainerListener listener) {
		super.removeListener(listener);
		listeners.remove(listener);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int time = te.getTime();
		boolean isWorking = te.getIsWorking();
		for (int i = 0; i < listeners.size(); i++) {
			if (lastTime != time)
				NetworkHelper.updateClientTileEntity(listeners.get(i), te.getPos(), 1, time);
			if (lastIsWorking != isWorking)
				NetworkHelper.updateClientTileEntity(listeners.get(i), te.getPos(), 2, isWorking ? 1 : 0);
		}
		lastTime = time;
		lastIsWorking = isWorking;
	}
}
