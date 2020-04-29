package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.PortablePanelInventory;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ContainerPortablePanel extends ContainerBase<PortablePanelInventory> {
	private EntityPlayer player;
	
	public ContainerPortablePanel(EntityPlayer player) {
		super(new PortablePanelInventory(player.getHeldItemMainhand(), "Item Inventory"));
		this.player = player;

		addSlotToContainer(new SlotFilter(te, 0, 174, 17));
		addSlotToContainer(new SlotFilter(te, 1, 174, 35));

		addPlayerInventoryTopSlots(player, 8, 188);
	}

	@Override
	public void detectAndSendChanges() {
		processCard();
		super.detectAndSendChanges();
	}
	
	private void processCard() {
		ItemStack card = te.getStackInSlot(PortablePanelInventory.SLOT_CARD);
		if (card.isEmpty())
			return;

		Item item = card.getItem();
		if (!(item instanceof ItemCardMain))
			return;

		ItemCardReader reader = new ItemCardReader(card);
		ItemCardMain.updateCardNBT(player.world, player.getPosition(), reader, te.getStackInSlot(PortablePanelInventory.SLOT_UPGRADE_RANGE));
	}
}
