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
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class ContainerPortablePanel extends ContainerBase<InventoryPortablePanel> {
	private static final String SHOW_BARS = "showBars";
	private PlayerEntity player;
	private static final int OFF_HAND_SLOT = 40; // PlayerInventory.OFF_HAND_SLOT
	private final int panelSlot;

	public ContainerPortablePanel(int windowId, PlayerInventory inventory, PacketByteBuf data) {
		this(windowId, inventory, data.readBoolean() ? Hand.OFF_HAND : Hand.MAIN_HAND);
	}
	public ContainerPortablePanel(int windowId, PlayerInventory inventory, Hand hand) {
		super(new InventoryPortablePanel(inventory.player.getStackInHand(hand), hand), ModContainerTypes.portable_panel, windowId);
		this.player = inventory.player;
		this.panelSlot = hand == Hand.OFF_HAND ? OFF_HAND_SLOT : inventory.selectedSlot;

		addSlot(new SlotCard(te, 0, 174, 17));
		addSlot(new SlotRange(te, 1, 174, 35));

		addPlayerInventoryTopSlots(inventory, 8, 188);
	}

	// the open panel stays in the hand, so its slot is locked
	@Override
	protected void addPlayerInventoryTopSlots(Inventory inventory, int width, int height) {
		for (int col = 0; col < 9; col++)
			if (col == panelSlot)
				addSlot(new LockedSlot(inventory, col, width + col * 18, height - 24));
			else
				addSlot(new Slot(inventory, col, width + col * 18, height - 24));
	}

	@Override
	public ItemStack onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		// number keys (or F for the off hand) would swap the panel out of its slot
		if (actionType == SlotActionType.SWAP && button == panelSlot)
			return ItemStack.EMPTY;
		return super.onSlotClick(slotIndex, button, actionType, player);
	}

	public boolean getShowBars() {
		NbtCompound tag = te.getParent().getTag();
		return tag != null && tag.getBoolean(SHOW_BARS);
	}

	public void setShowBars(boolean value) {
		te.getParent().getOrCreateTag().putBoolean(SHOW_BARS, value);
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

	private static class LockedSlot extends Slot {
		public LockedSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean canTakeItems(PlayerEntity player) {
			return false;
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return false;
		}
	}
}
