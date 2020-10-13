package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.items.cards.MainCardItem;
import com.zuxelus.energycontrol.screen.handlers.PortablePanelScreenHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class PortablePanelInventory extends InventoryItem implements NamedScreenHandlerFactory {
	public static final byte SLOT_CARD = 0;
	public static final byte SLOT_UPGRADE_RANGE = 1;

	public PortablePanelInventory(ItemStack parent) {
		super(parent);
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack) { // ISlotItemFilter
		switch (slot) {
		case SLOT_CARD:
			return stack.getItem() instanceof MainCardItem;
		case SLOT_UPGRADE_RANGE:
			return stack.getItem() instanceof UpgradeRangeItem;
		default:
			return false;
		}
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new PortablePanelScreenHandler(syncId, inv);
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText("item.energycontrol.portable_panel");
	}
}
