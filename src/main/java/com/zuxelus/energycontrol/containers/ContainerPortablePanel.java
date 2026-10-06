package com.zuxelus.energycontrol.containers;

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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ContainerPortablePanel extends ContainerBase<InventoryPortablePanel> {
	private static final String SHOW_BARS = "showBars";
	public static final int OFF_HAND_SLOT = 40; // Inventory.SLOT_OFFHAND
	private Player player;
	// inventory slot of the open panel; it stays in the hand, so it is locked
	private final int panelSlot;

	// client side: the server sends which hand holds the panel
	public ContainerPortablePanel(int windowId, Inventory inventory, RegistryFriendlyByteBuf data) {
		this(windowId, inventory, data.readBoolean() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
	}

	public ContainerPortablePanel(int windowId, Inventory inventory, InteractionHand hand) {
		super(new InventoryPortablePanel(inventory.player.getItemInHand(hand), hand), ModContainerTypes.portable_panel.get(), windowId);
		this.player = inventory.player;
		this.panelSlot = hand == InteractionHand.OFF_HAND ? OFF_HAND_SLOT : inventory.getSelectedSlot();

		addSlot(new SlotCard(te, 0, 174, 17));
		addSlot(new SlotRange(te, 1, 174, 35));

		addPlayerInventoryTopSlots(inventory, 8, 188);
	}

	@Override
	protected void addPlayerInventoryTopSlots(Inventory inventory, int width, int height) {
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
		ItemStackHelper.update(te.getParent(), tag -> tag.putBoolean(SHOW_BARS, value));
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
