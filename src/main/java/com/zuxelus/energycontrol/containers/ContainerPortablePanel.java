package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.api.IItemCard;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotLocked;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ContainerPortablePanel extends ContainerBase<InventoryPortablePanel> {
	private static final String SHOW_BARS = "showBars";
	private Player player;
	public static final int OFF_HAND_SLOT = 40; // PlayerInventory.OFF_HAND_SLOT
	private final int panelSlot;

	public ContainerPortablePanel(int windowId, Inventory inventory, InteractionHand hand) {
		super(new InventoryPortablePanel(inventory.player.getItemInHand(hand), hand), ModContainerTypes.portable_panel, windowId);
		this.player = inventory.player;
		this.panelSlot = hand == InteractionHand.OFF_HAND ? OFF_HAND_SLOT : inventory.getSelectedSlot();

		addSlot(new SlotCard(te, 0, 174, 17));
		addSlot(new SlotRange(te, 1, 174, 35));

		addPlayerInventoryTopSlots(inventory, 8, 188);
	}

	// the open panel stays in the hand, so its slot is locked
	@Override
	protected void addPlayerInventoryTopSlots(Container inventory, int width, int height) {
		for (int col = 0; col < 9; col++)
			if (col == panelSlot)
				addSlot(new SlotLocked(inventory, col, width + col * 18, height - 24));
			else
				addSlot(new Slot(inventory, col, width + col * 18, height - 24));
	}

	@Override
	public void clicked(int slotIndex, int button, ContainerInput actionType, Player player) {
		// number keys (or F for the off hand) would swap the panel out of its slot
		if (actionType == ContainerInput.SWAP && button == panelSlot)
			return;
		super.clicked(slotIndex, button, actionType, player);
	}

	public boolean getShowBars() {
		CompoundTag tag = ItemStackHelper.getTag(te.getParent());
		return tag != null && tag.getBooleanOr(SHOW_BARS, false);
	}

	public void setShowBars(boolean value) {
		ItemStackHelper.updateTag(te.getParent(), tag -> tag.putBoolean(SHOW_BARS, value));
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
		if (!ItemCardMain.isCard(card))
			return;

		ItemCardReader reader = new ItemCardReader(card);
		ItemCardMain.updateCardNBT((IItemCard) item, player.level(), player.blockPosition(), reader, te.getItem(InventoryPortablePanel.SLOT_UPGRADE_RANGE));
	}
}
