package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.items.InventoryCardHolder;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.SlotActionType;

public class ContainerCardHolder extends ContainerBase<InventoryCardHolder> {
	// menu slot showing the card holder itself (the selected hotbar slot); its cards are saved into that stack
	private int holderSlot = -1;

	public ContainerCardHolder(int windowId, PlayerInventory inventory, PacketByteBuf data) {
		this(windowId, inventory);
	}
	public ContainerCardHolder(int windowId, PlayerInventory inventory) {
		super(new InventoryCardHolder(inventory.player.getMainHandStack()), ModContainerTypes.card_holder, windowId);
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new SlotFilter(te, j + i * 9, 8 + j * 18, 18 + i * 18));

		addPlayerInventorySlots(inventory, 167 + 18 * 3);
		// the hotbar is added last, one slot per column
		holderSlot = slots.size() - 9 + inventory.selectedSlot;
	}

	// the open card holder must not be moved, thrown or swapped away while its GUI is open
	@Override
	public ItemStack onSlotClick(int slotIndex, int button, SlotActionType action, PlayerEntity player) {
		if (slotIndex == holderSlot && holderSlot >= 0)
			return ItemStack.EMPTY;
		if (action == SlotActionType.SWAP && button == player.inventory.selectedSlot)
			return ItemStack.EMPTY;
		return super.onSlotClick(slotIndex, button, action, player);
	}
}
