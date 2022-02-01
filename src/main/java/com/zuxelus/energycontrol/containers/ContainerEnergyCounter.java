package com.zuxelus.energycontrol.containers;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerEnergyCounter extends Container {
	public TileEntityEnergyCounter te;
	private int lastTotal;
	private final List<IContainerListener> listeners = Lists.newArrayList();

	public ContainerEnergyCounter(int windowId, PlayerInventory inventory, PacketBuffer data) {
		this(windowId, inventory, (TileEntityEnergyCounter) ContainerBase.getTileEntity(inventory, data));
	}

	public ContainerEnergyCounter(int windowId, PlayerInventory inventory, TileEntityEnergyCounter te) {
		super(ModContainerTypes.energy_counter.get(), windowId);
		this.te = te;
		addPlayerInventorySlots(inventory, 166);
	}

	private void addPlayerInventorySlots(PlayerInventory inventory, int height) {
		addPlayerInventorySlots(inventory, 178, height);
	}

	private void addPlayerInventorySlots(PlayerInventory inventory, int width, int height) {
		int xStart = (width - 162) / 2;
		for (int row = 0; row < 3; row++)
			for (int i = 0; i < 9; i++)
				addSlot(new Slot(inventory, i + row * 9 + 9, xStart + i * 18, height - 82 + row * 18));

		addPlayerInventoryTopSlots(inventory, xStart, height);
	}

	private void addPlayerInventoryTopSlots(PlayerInventory inventory, int width, int height) {
		for (int col = 0; col < 9; col++)
			addSlot(new Slot(inventory, col, width + col * 18, height - 24));
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return player.distanceToSqr(te.getBlockPos().getX() + 0.5D, te.getBlockPos().getY() + 0.5D, te.getBlockPos().getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void addSlotListener(IContainerListener listener) {
		super.addSlotListener(listener);
		listeners.add(listener);
	}

	@Override
	public void removeSlotListener(IContainerListener listener) {
		super.removeSlotListener(listener);
		listeners.remove(listener);
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		int total = te.getTotal();
		for (IContainerListener listener : listeners)
			if (lastTotal != total)
				NetworkHelper.updateClientTileEntity(listener, te.getBlockPos(), 1, total);
		lastTotal = total;
	}
}
