package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.InventoryCardHolder;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;

public class ContainerCardHolder extends ContainerBase<InventoryCardHolder> {

	public ContainerCardHolder(int windowId, PlayerInventory inventory, PacketByteBuf data) {
		this(windowId, inventory);
	}
	public ContainerCardHolder(int windowId, PlayerInventory inventory) {
		super(new InventoryCardHolder(inventory.player.getMainHandStack()), ModContainerTypes.card_holder, windowId);
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new SlotFilter(te, j + i * 9, 8 + j * 18, 18 + i * 18));

		addPlayerInventorySlots(inventory, 167 + 18 * 3);
	}
}
