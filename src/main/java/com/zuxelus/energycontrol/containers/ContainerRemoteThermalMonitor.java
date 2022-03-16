package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;

public class ContainerRemoteThermalMonitor extends ContainerBase<TileEntityRemoteThermalMonitor> {

	public ContainerRemoteThermalMonitor(int windowId, PlayerInventory inventory, PacketBuffer data) {
		this(windowId, inventory, (TileEntityRemoteThermalMonitor) getTileEntity(inventory, data));
	}

	public ContainerRemoteThermalMonitor(int windowId, PlayerInventory inventory, TileEntityRemoteThermalMonitor te) {
		super(te, ModContainerTypes.remote_thermo.get(), windowId, ModItems.remote_thermo.get(), IWorldPosCallable.create(te.getLevel(), te.getBlockPos()));

		addSlot(new SlotCard(te, TileEntityRemoteThermalMonitor.SLOT_CARD, 9, 53));
		addSlot(new SlotRange(te, TileEntityRemoteThermalMonitor.SLOT_UPGRADE_RANGE, 27, 53));
		addPlayerInventorySlots(inventory, 180, 166);
	}
}
