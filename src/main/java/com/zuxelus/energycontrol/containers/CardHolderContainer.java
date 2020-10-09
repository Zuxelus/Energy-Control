package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.CardHolderInventory;

import net.minecraft.entity.player.PlayerEntity;

public class CardHolderContainer extends BaseContainer<CardHolderInventory> {

	public CardHolderContainer(int syncId, PlayerEntity player) {
		super(syncId, player.inventory, new CardHolderInventory(player.getMainHandStack()));
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new SlotFilter(be, j + i * 9, 8 + j * 18, 18 + i * 18));

		addPlayerInventorySlots(player, 167 + 18 * 3, ModItems.CARD_HOLDER_ITEM);
	}
}
