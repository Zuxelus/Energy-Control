package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class ContainerRangeTrigger extends ContainerBase<TileEntityRangeTrigger> {

	public ContainerRangeTrigger(int windowId, Inventory inventory, FriendlyByteBuf data) {
		this(windowId, inventory, (TileEntityRangeTrigger) getBlockEntity(inventory, data));
	}

	public ContainerRangeTrigger(int windowId, Inventory inventory, TileEntityRangeTrigger te) {
		super(te, ModContainerTypes.range_trigger.get(), windowId, ModItems.range_trigger.get(), ContainerLevelAccess.create(te.getLevel(), te.getBlockPos()));

		addSlot(new SlotCard(te, 0, 8, 21));
		addSlot(new SlotRange(te, 1, 8, 39));
		addPlayerInventorySlots(inventory, 190);
	}
}
