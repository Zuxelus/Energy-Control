package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;

public class ContainerRangeTrigger extends ContainerBase<TileEntityRangeTrigger> {

	public ContainerRangeTrigger(int windowId, PlayerInventory inventory, PacketByteBuf data) {
		this(windowId, inventory, (TileEntityRangeTrigger) getBlockEntity(inventory, data));
	}

	public ContainerRangeTrigger(int windowId, PlayerInventory inventory, TileEntityRangeTrigger te) {
		super(te, ModContainerTypes.range_trigger, windowId, ModItems.range_trigger, ScreenHandlerContext.create(te.getWorld(), te.getPos()));

		addSlot(new SlotCard(te, 0, 8, 21));
		addSlot(new SlotRange(te, 1, 8, 39));
		addPlayerInventorySlots(inventory, 190);
	}
}
