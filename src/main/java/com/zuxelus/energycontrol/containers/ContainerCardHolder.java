package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.items.InventoryCardHolder;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;
import com.zuxelus.zlib.containers.slots.SlotLocked;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;

public class ContainerCardHolder extends ContainerBase<InventoryCardHolder> {
	// inventory slot of the open card holder; its cards are saved into that stack, so it is locked
	private final int holderSlot;

	public ContainerCardHolder(int windowId, Inventory inventory, InteractionHand hand) {
		super(new InventoryCardHolder(inventory.player.getItemInHand(hand), hand), ModContainerTypes.card_holder, windowId);
		this.holderSlot = hand == InteractionHand.OFF_HAND ? ContainerPortablePanel.OFF_HAND_SLOT : inventory.getSelectedSlot();
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new SlotFilter(te, j + i * 9, 8 + j * 18, 18 + i * 18));

		addPlayerInventorySlots(inventory, 167 + 18 * 3);
	}

	@Override
	protected void addPlayerInventoryTopSlots(Container inventory, int width, int height) {
		for (int col = 0; col < 9; col++)
			if (col == holderSlot)
				addSlot(new SlotLocked(inventory, col, width + col * 18, height - 24));
			else
				addSlot(new Slot(inventory, col, width + col * 18, height - 24));
	}

	@Override
	public void clicked(int slotIndex, int button, ContainerInput action, Player player) {
		// number keys (or F for the off hand) would swap the card holder out of its slot
		if (action == ContainerInput.SWAP && button == holderSlot)
			return;
		super.clicked(slotIndex, button, action, player);
	}
}
