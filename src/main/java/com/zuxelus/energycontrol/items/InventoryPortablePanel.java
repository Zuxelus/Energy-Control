package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
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

public class InventoryPortablePanel extends ItemInventory implements INamedContainerProvider {
	public static final byte SLOT_CARD = 0;
	public static final byte SLOT_UPGRADE_RANGE = 1;

	public InventoryPortablePanel(ItemStack parent) {
		super(parent);
	}

	@Override
	public int getContainerSize() {
		return 2;
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD:
			return stack.getItem() instanceof ItemCardMain;
		case SLOT_UPGRADE_RANGE:
			return !stack.isEmpty() && stack.getItem().equals(ModItems.upgrade_range.get());
		default:
			return false;
		}
	}

	// INamedContainerProvider
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerPortablePanel(windowId, inventory);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModItems.portable_panel.get().getDescriptionId());
	}
}
