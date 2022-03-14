package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermo;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerRemoteThermo extends ContainerBase<TileEntityRemoteThermo> {

	public ContainerRemoteThermo(EntityPlayer player, TileEntityRemoteThermo remoteThermo) {
		super(remoteThermo);
		// energy charger
		// addSlotToContainer(new SlotFilter(remoteThermo, 0, 13, 53));
		// upgrades
		addSlotToContainer(new SlotCard(remoteThermo, 1, 9, 53));
		addSlotToContainer(new SlotRange(remoteThermo, 2, 27, 53));
		// inventory
		addPlayerInventorySlots(player, 180, 166);
	}
}
