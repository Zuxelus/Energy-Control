package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.items.InventoryCardHolder;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerCardHolder extends ContainerBase<InventoryCardHolder> {

	public ContainerCardHolder(EntityPlayer player) {
		super(new InventoryCardHolder(player.getHeldItem(), "item.card_holder.name"));
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new SlotFilter(te, j + i * 9, 8 + j * 18, 18 + i * 18));

		addPlayerInventorySlots(player, 167 + 18 * 3);
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		te.writeToParentNBT(player);
	}
}
