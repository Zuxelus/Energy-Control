package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.items.cards.MainCardItem;
import com.zuxelus.energycontrol.screen.handlers.CardHolderScreenHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CardHolderInventory extends InventoryItem implements NamedScreenHandlerFactory {

	public CardHolderInventory(ItemStack parent) {
		super(parent);
	}

	@Override
	public int size() {
		return 54;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack) {
		return stack.getItem() instanceof MainCardItem;
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new CardHolderScreenHandler(syncId, inv);
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText("item.energycontrol.card_holder");
	}
}
