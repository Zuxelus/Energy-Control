package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
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

public class InventoryPortablePanel extends ItemInventory implements MenuProvider {
	public static final byte SLOT_CARD = 0;
	public static final byte SLOT_UPGRADE_RANGE = 1;

	private final InteractionHand hand;

	public InventoryPortablePanel(ItemStack parent, InteractionHand hand) {
		super(parent);
		this.hand = hand;
	}

	@Override
	public int getContainerSize() {
		return 2;
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD:
			return ItemCardMain.isCard(stack);
		case SLOT_UPGRADE_RANGE:
			return !stack.isEmpty() && stack.getItem().equals(ModItems.upgrade_range.get());
		default:
			return false;
		}
	}

	// MenuProvider
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPortablePanel(windowId, inventory, hand);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(ModItems.portable_panel.get().getDescriptionId());
	}
}
