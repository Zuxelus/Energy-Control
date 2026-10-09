package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.containers.ContainerCardHolder;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.zlib.items.ItemInventory;

import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class InventoryCardHolder extends ItemInventory implements MenuProvider {

	private final InteractionHand hand;

	public InventoryCardHolder(ItemStack parent, InteractionHand hand) {
		super(parent);
		this.hand = hand;
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
		return ItemCardMain.isCard(stack);
	}

	// MenuProvider
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerCardHolder(windowId, inventory, hand);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(ModItems.card_holder.get().getDescriptionId());
	}
}
