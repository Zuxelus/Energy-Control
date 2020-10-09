package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.PortablePanelInventory;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.MainCardItem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class PortablePanelContainer extends BaseContainer<PortablePanelInventory> {
	private PlayerEntity player;
	
	public PortablePanelContainer(int syncId, PlayerEntity player) {
		super(syncId, player.inventory, new PortablePanelInventory(player.getMainHandStack()));
		this.player = player;

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
		ItemStack card = be.getInvStack(PortablePanelInventory.SLOT_CARD);
		if (card.isEmpty())
			return;

		Item item = card.getItem();
		if (!(item instanceof MainCardItem))
			return;

		ItemCardReader reader = new ItemCardReader(card);
		((MainCardItem) item).updateCardNBT(player.world, new BlockPos (player.getX(), player.getY(), player.getZ()), reader, be.getInvStack(PortablePanelInventory.SLOT_UPGRADE_RANGE));
	}
}
