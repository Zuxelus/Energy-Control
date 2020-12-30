package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerRangeTrigger extends ContainerBase<TileEntityRangeTrigger> {

	public ContainerRangeTrigger(EntityPlayer player, TileEntityRangeTrigger trigger) {
		super(trigger);
		// card
		addSlotToContainer(new SlotFilter(trigger, 0, 8, 21));
		// upgrade
		addSlotToContainer(new SlotFilter(trigger, 1, 8, 39));
		// inventory
		addPlayerInventorySlots(player, 190);
	}
}