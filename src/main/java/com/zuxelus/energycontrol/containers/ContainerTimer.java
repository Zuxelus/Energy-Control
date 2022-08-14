package com.zuxelus.energycontrol.containers;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class ContainerTimer extends AbstractContainerMenu {
	public TileEntityTimer te;
	private int lastTime;
	private boolean lastIsWorking;
	public List<ServerPlayer> listeners = Lists.newArrayList();

	public ContainerTimer(int windowId, Inventory inventory, FriendlyByteBuf data) {
		this(windowId, inventory, (TileEntityTimer) ContainerBase.getBlockEntity(inventory, data));
	}

	public ContainerTimer(int windowId, Inventory inventory, TileEntityTimer te) {
		super(ModContainerTypes.timer.get(), windowId);
		this.te = te;
		lastTime = 0;
	}

	@Override
	public boolean stillValid(Player player) {
		return player.distanceToSqr(te.getBlockPos().getX() + 0.5D, te.getBlockPos().getY() + 0.5D, te.getBlockPos().getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		int time = te.getTime();
		boolean isWorking = te.getIsWorking();
		for (ServerPlayer listener : listeners) {
			if (lastTime != time)
				NetworkHelper.updateClientTileEntity(listener, te.getBlockPos(), 1, time);
			if (lastIsWorking != isWorking)
				NetworkHelper.updateClientTileEntity(listener, te.getBlockPos(), 2, isWorking ? 1 : 0);
		}
		lastTime = time;
		lastIsWorking = isWorking;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return null;
	}
}
