package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.blockentities.InfoPanelBlockEntity;

import net.minecraft.entity.player.PlayerInventory;

public class InfoPanelContainer extends BaseContainer<InfoPanelBlockEntity> {

	public InfoPanelContainer(int syncId, PlayerInventory playerInventory, InfoPanelBlockEntity panel) {
		super(syncId, playerInventory, panel);

		addSlot(new SlotFilter(panel, 0, 8, 24 + 18));
		// range upgrade
		addSlot(new SlotFilter(panel, 1, 8, 24 + 18 * 2));
		// color upgrade
		addSlot(new SlotFilter(panel, 2, 8, 24 + 18 * 3));
		// touch upgrade
		addSlot(new SlotFilter(panel, 3, 8, 24 + 18 * 4));
		// inventory
		addPlayerInventorySlots(playerInventory.player, 201);
	}
}
