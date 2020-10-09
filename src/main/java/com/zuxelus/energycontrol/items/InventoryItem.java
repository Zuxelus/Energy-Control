package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.containers.ISlotItemFilter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;

public abstract class InventoryItem implements Inventory, ISlotItemFilter {
	private final ItemStack parent;
	protected DefaultedList<ItemStack> inventory;

	public InventoryItem(ItemStack parent) {
		this.parent = parent;
		inventory = DefaultedList.ofSize(getInvSize(), ItemStack.EMPTY);
		readFromParentNBT();
	}

	private void readFromParentNBT() {
		CompoundTag tag = parent.getTag();
		if (tag == null) {
			tag = new CompoundTag();
			parent.setTag(tag);
		}
		Inventories.fromTag(tag, inventory);
	}

	private void writeToParentNBT() {
		CompoundTag tag = parent.getTag();
		if (tag == null) {
			tag = new CompoundTag();
			parent.setTag(tag);
		}
		Inventories.toTag(tag, inventory);
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return canInsert(slot, stack);
	}

	@Override
	public void clear() {
		inventory.clear();
		markDirty();
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemstack : inventory)
			if (!itemstack.isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= 0 && slot < inventory.size() ? (ItemStack)inventory.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack stack = Inventories.splitStack(inventory, slot, amount);
		if (!stack.isEmpty())
			markDirty();
		return stack;
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		ItemStack stack = getInvStack(slot);
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		inventory.set(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > getInvMaxStackAmount())
			stack.setCount(getInvMaxStackAmount());
		markDirty();
	}

	@Override
	public void markDirty() {
		writeToParentNBT();
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return true;
	}
}
