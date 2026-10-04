package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.screen.ScreenHandlerContext;

public class ContainerRemoteThermalMonitor extends ContainerBase<TileEntityRemoteThermalMonitor> {

	public ContainerRemoteThermalMonitor(int windowId, PlayerInventory inventory, BlockPos data) {
		this(windowId, inventory, (TileEntityRemoteThermalMonitor) getBlockEntity(inventory, data));
	}

	public ContainerRemoteThermalMonitor(int windowId, PlayerInventory inventory, TileEntityRemoteThermalMonitor te) {
		super(te, ModContainerTypes.remote_thermo, windowId, ModItems.remote_thermo, ScreenHandlerContext.create(te.getWorld(), te.getPos()));

		addSlot(new SlotCard(te, TileEntityRemoteThermalMonitor.SLOT_CARD, 9, 53));
		addSlot(new SlotRange(te, TileEntityRemoteThermalMonitor.SLOT_UPGRADE_RANGE, 27, 53));
		addPlayerInventorySlots(inventory, 180, 166);
	}
}
