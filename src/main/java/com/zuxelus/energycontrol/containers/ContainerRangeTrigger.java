package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import com.zuxelus.zlib.containers.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerRangeTrigger extends ContainerBase<TileEntityRangeTrigger> {

	public ContainerRangeTrigger(EntityPlayer player, TileEntityRangeTrigger trigger) {
		super(trigger);
		// card
		addSlotToContainer(new SlotCard(trigger, 0, 8, 21));
		// upgrade
		addSlotToContainer(new SlotRange(trigger, 1, 8, 39));
		// inventory
		addPlayerInventorySlots(player, 190);
	}
}
