package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.containers.ContainerCardHolder;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.zlib.items.ItemInventory;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class InventoryCardHolder extends ItemInventory implements ExtendedMenuProvider<BlockPos> {

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

	// NamedScreenHandlerFactory
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerCardHolder(windowId, inventory);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(ModItems.card_holder.getDescriptionId());
	}

	// the screen type is extended; the container needs no extra data
	@Override
	public BlockPos getScreenOpeningData(ServerPlayer player) {
		return BlockPos.ZERO;
	}
}
