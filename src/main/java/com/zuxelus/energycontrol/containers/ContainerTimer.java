package com.zuxelus.energycontrol.containers;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ContainerTimer extends ScreenHandler {
	public TileEntityTimer te;
	private int lastTime;
	private boolean lastIsWorking;
	public List<ServerPlayerEntity> containerListeners = Lists.newArrayList();

	public ContainerTimer(int windowId, PlayerInventory inventory, PacketByteBuf data) {
		this(windowId, inventory, (TileEntityTimer) ContainerBase.getBlockEntity(inventory, data));
	}

	public ContainerTimer(int windowId, PlayerInventory inventory, TileEntityTimer te) {
		super(ModContainerTypes.timer, windowId);
		this.te = te;
		lastTime = 0;
		if (inventory.player instanceof ServerPlayerEntity)
			containerListeners.add((ServerPlayerEntity) inventory.player);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return player.squaredDistanceTo(te.getPos().getX() + 0.5D, te.getPos().getY() + 0.5D, te.getPos().getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();
		int time = te.getTime();
		boolean isWorking = te.getIsWorking();
		for (ServerPlayerEntity listener : containerListeners) {
			if (lastTime != time)
				NetworkHelper.updateClientTileEntity(listener, te.getPos(), 1, time);
			if (lastIsWorking != isWorking)
				NetworkHelper.updateClientTileEntity(listener, te.getPos(), 2, isWorking ? 1 : 0);
		}
		lastTime = time;
		lastIsWorking = isWorking;
	}
}
