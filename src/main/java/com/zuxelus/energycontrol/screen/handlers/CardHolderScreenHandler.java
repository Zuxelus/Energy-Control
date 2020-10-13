package com.zuxelus.energycontrol.screen.handlers;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.CardHolderInventory;

import net.minecraft.entity.player.PlayerInventory;

public class CardHolderScreenHandler extends BaseScreenHandler<CardHolderInventory> {

	public CardHolderScreenHandler(int syncId, PlayerInventory inventory) {
		super(ModItems.CARD_HOLDER_SCREEN_HANDLER, syncId, inventory, new CardHolderInventory(inventory.player.getMainHandStack()));
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new SlotFilter(be, j + i * 9, 8 + j * 18, 18 + i * 18));

		addPlayerInventorySlots(inventory.player, 167 + 18 * 3, ModItems.CARD_HOLDER_ITEM);
	}
}
