package com.zuxelus.energycontrol.screen.handlers;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.PortablePanelInventory;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.MainCardItem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class PortablePanelScreenHandler extends BaseScreenHandler<PortablePanelInventory> {
	private PlayerEntity player;
	
	public PortablePanelScreenHandler(int syncId, PlayerInventory inventory) {
		super(ModItems.PORTABLE_PANEL_SCREEN_HANDLER, syncId, inventory, new PortablePanelInventory(inventory.player.getMainHandStack()));
		this.player = inventory.player;

		addSlot(new SlotFilter(be, 0, 174, 17));
		addSlot(new SlotFilter(be, 1, 174, 35));

		addPlayerInventoryTopSlots(player, 8, 188, ModItems.PORTABLE_PANEL_ITEM);
	}

	@Override
	public void sendContentUpdates() {
		processCard();
		super.sendContentUpdates();
	}

	private void processCard() {
		ItemStack card = be.getStack(PortablePanelInventory.SLOT_CARD);
		if (card.isEmpty())
			return;

		Item item = card.getItem();
		if (!(item instanceof MainCardItem))
			return;

		ItemCardReader reader = new ItemCardReader(card);
		((MainCardItem) item).updateCardNBT(player.world, new BlockPos (player.getX(), player.getY(), player.getZ()), reader, be.getStack(PortablePanelInventory.SLOT_UPGRADE_RANGE));
	}
}
