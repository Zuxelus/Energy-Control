package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

public class ContainerPortablePanel extends ContainerBase<InventoryPortablePanel> {
	private PlayerEntity player;

	public ContainerPortablePanel(int windowId, PlayerInventory inventory, PacketByteBuf data) {
		this(windowId, inventory);
	}
	public ContainerPortablePanel(int windowId, PlayerInventory inventory) {
		super(new InventoryPortablePanel(inventory.player.getMainHandStack()), ModContainerTypes.portable_panel, windowId);
		this.player = inventory.player;

		addSlot(new SlotCard(te, 0, 174, 17));
		addSlot(new SlotRange(te, 1, 174, 35));

		addPlayerInventoryTopSlots(inventory, 8, 188);
	}

	@Override
	public void sendContentUpdates() {
		processCard();
		super.sendContentUpdates();
	}

	private void processCard() {
		ItemStack card = te.getStack(InventoryPortablePanel.SLOT_CARD);
		if (card.isEmpty())
			return;

		Item item = card.getItem();
		if (!(item instanceof ItemCardMain))
			return;

		ItemCardReader reader = new ItemCardReader(card);
		((ItemCardMain) item).updateCardNBT(player.world, player.getBlockPos(), reader, te.getStack(InventoryPortablePanel.SLOT_UPGRADE_RANGE));
	}
}
