package com.zuxelus.zlib.containers;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

public abstract class ContainerBase<T extends IInventory> extends Container {
	public final T te;
	public final Block block;
	public final IWorldPosCallable posCallable;

	protected ContainerBase(T te, ContainerType<?> type, int id, Block block, IWorldPosCallable posCallable) {
		super(type, id);
		this.te = te;
		this.block = block;
		this.posCallable = posCallable;
	}

	protected void addPlayerInventorySlots(PlayerInventory player, int height) {
		addPlayerInventorySlots(player, 178, height);
	}
	
	protected void addPlayerInventorySlots(PlayerInventory player, int width, int height) {
		int xStart = (width - 162) / 2;
		for (int row = 0; row < 3; row++)
			for (int i = 0; i < 9; i++)
				addSlot(new Slot(player, i + row * 9 + 9, xStart + i * 18, height - 82 + row * 18));

		addPlayerInventoryTopSlots(player, xStart, height);
	}

	protected void addPlayerInventoryTopSlots(PlayerInventory player, int width, int height) {
		for (int col = 0; col < 9; col++)
			addSlot(new Slot(player, col, width + col * 18, height - 24));
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return isWithinUsableDistance(posCallable, player, block);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index) {
		Slot slot = inventorySlots.get(index);
		if (slot == null || !slot.getHasStack())
			return ItemStack.EMPTY;

		ItemStack stack = slot.getStack();
		ItemStack result = stack.copy();
		
		int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();
		if (index < containerSlots) {
			if (!mergeItemStack(stack, containerSlots, inventorySlots.size(), true))
				return ItemStack.EMPTY;
		} else if (!mergeItemStack(stack, 0, containerSlots, false))
			return ItemStack.EMPTY;
		if (stack.getCount() == 0)
			slot.putStack(ItemStack.EMPTY);
		else
			slot.onSlotChanged();
		if (stack.getCount() == result.getCount())
			return ItemStack.EMPTY;
		slot.onTake(player, stack);
		return result;
	}
}
