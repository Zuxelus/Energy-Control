package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.items.InventoryCardHolder;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;

public class ContainerCardHolder extends ContainerBase<InventoryCardHolder> {
	// menu slot showing the card holder itself (the selected hotbar slot); its cards are saved into that stack
	private int holderSlot = -1;

	public ContainerCardHolder(int windowId, Inventory inventory, BlockPos data) {
		this(windowId, inventory);
	}
	public ContainerCardHolder(int windowId, Inventory inventory) {
		super(new InventoryCardHolder(inventory.player.getMainHandItem()), ModContainerTypes.card_holder, windowId);
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new SlotFilter(te, j + i * 9, 8 + j * 18, 18 + i * 18));

		addPlayerInventorySlots(inventory, 167 + 18 * 3);
		for (Slot slot : slots)
			if (slot.container == inventory && slot.getContainerSlot() == inventory.getSelectedSlot())
				holderSlot = slot.index;
	}

	// the open card holder must not be moved, thrown or swapped away while its GUI is open
	@Override
	public void clicked(int slotIndex, int button, ContainerInput action, Player player) {
		if (slotIndex == holderSlot && holderSlot >= 0)
			return;
		if (action == ContainerInput.SWAP && button == player.getInventory().getSelectedSlot())
			return;
		super.clicked(slotIndex, button, action, player);
	}
}
