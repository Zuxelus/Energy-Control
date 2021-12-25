package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.containers.ContainerCardHolder;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.zlib.items.ItemInventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class InventoryCardHolder extends ItemInventory implements NamedScreenHandlerFactory {

	public InventoryCardHolder(ItemStack parent) {
		super(parent);
	}

	@Override
	public int size() {
		return 54;
	}

	@Override
	public boolean isValid(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) {
		return stack.getItem() instanceof ItemCardMain;
	}

	// NamedScreenHandlerFactory
	@Override
	public ScreenHandler createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerCardHolder(windowId, inventory);
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText(ModItems.card_holder.getTranslationKey());
	}
}
