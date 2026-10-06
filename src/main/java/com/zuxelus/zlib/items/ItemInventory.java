package com.zuxelus.zlib.items;

import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public abstract class ItemInventory implements Container, ISlotItemFilter {

	private final ItemStack parent;
	protected NonNullList<ItemStack> inventory;

	public ItemInventory(ItemStack parent) {
		this.parent = parent;
		inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		readFromParentNBT();
	}

	public ItemStack getParent() {
		return parent;
	}

	private void readFromParentNBT() {
		parent.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(inventory);
	}

	private void writeToParentNBT() {
		parent.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(inventory));
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : inventory)
			if (!stack.isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot >= 0 && slot < getContainerSize() ? inventory.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack stack = ContainerHelper.removeItem(inventory, index, count);
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = getItem(slot);
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		inventory.set(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack.setCount(getMaxStackSize());
		setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void setChanged() {
		writeToParentNBT();
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public void clearContent() {
		inventory.clear();
	}
}
