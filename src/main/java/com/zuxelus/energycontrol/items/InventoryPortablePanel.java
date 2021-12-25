package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.containers.ContainerPortablePanel;
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

public class InventoryPortablePanel extends ItemInventory implements NamedScreenHandlerFactory {
	public static final byte SLOT_CARD = 0;
	public static final byte SLOT_UPGRADE_RANGE = 1;

	public InventoryPortablePanel(ItemStack parent) {
		super(parent);
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD:
			return stack.getItem() instanceof ItemCardMain;
		case SLOT_UPGRADE_RANGE:
			return !stack.isEmpty() && stack.getItem().equals(ModItems.upgrade_range);
		default:
			return false;
		}
	}

	// NamedScreenHandlerFactory
	@Override
	public ScreenHandler createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerPortablePanel(windowId, inventory);
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText(ModItems.portable_panel.getTranslationKey());
	}
}
