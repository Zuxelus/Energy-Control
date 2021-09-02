package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.containers.ContainerCardHolder;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.zlib.items.ItemInventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class InventoryCardHolder extends ItemInventory implements INamedContainerProvider {

	public InventoryCardHolder(ItemStack parent) {
		super(parent);
	}

	@Override
	public int getContainerSize() {
		return 54;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) {
		return stack.getItem() instanceof ItemCardMain;
	}

	// INamedContainerProvider
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerCardHolder(windowId, inventory);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModItems.card_holder.get().getDescriptionId());
	}
}
