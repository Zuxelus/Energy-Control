package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerRemoteThermalMonitor extends ContainerBase<TileEntityRemoteThermalMonitor> {

	public ContainerRemoteThermalMonitor(EntityPlayer player, TileEntityRemoteThermalMonitor te) {
		super(te);
		// upgrades
		addSlotToContainer(new SlotCard(te, 1, 9, 53));
		addSlotToContainer(new SlotRange(te, 2, 27, 53));
		// inventory
		addPlayerInventorySlots(player, 180, 166);
	}
}
