package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ContainerPortablePanel extends ContainerBase<InventoryPortablePanel> {
	private Player player;

	public ContainerPortablePanel(int windowId, Inventory inventory, FriendlyByteBuf data) {
		this(windowId, inventory);
	}
	public ContainerPortablePanel(int windowId, Inventory inventory) {
		super(new InventoryPortablePanel(inventory.player.getMainHandItem()), ModContainerTypes.portable_panel.get(), windowId);
		this.player = inventory.player;

		addSlot(new SlotCard(te, 0, 174, 17));
		addSlot(new SlotRange(te, 1, 174, 35));

		addPlayerInventoryTopSlots(inventory, 8, 188);
	}

	@Override
	public void broadcastChanges() {
		processCard();
		super.broadcastChanges();
	}

	private void processCard() {
		ItemStack card = te.getItem(InventoryPortablePanel.SLOT_CARD);
		if (card.isEmpty())
			return;

		Item item = card.getItem();
		if (!(item instanceof ItemCardMain))
			return;

		ItemCardReader reader = new ItemCardReader(card);
		((ItemCardMain) item).updateCardNBT(player.level(), player.blockPosition(), reader, te.getItem(InventoryPortablePanel.SLOT_UPGRADE_RANGE));
	}
}
