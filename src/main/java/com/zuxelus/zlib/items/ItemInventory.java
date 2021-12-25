package com.zuxelus.zlib.items;

import com.zuxelus.zlib.containers.slots.ISlotItemFilter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

public abstract class ItemInventory implements Inventory, ISlotItemFilter {

	private final ItemStack parent;
	protected DefaultedList<ItemStack> inventory;

	public ItemInventory(ItemStack parent) {
		this.parent = parent;
		inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY);
		readFromParentNBT();
	}

	private void readFromParentNBT() {
		NbtCompound tag = parent.getNbt();
		if (tag == null) {
			tag = new NbtCompound();
			parent.setNbt(tag);
		}

		NbtList list = tag.getList("Items", NbtElement.COMPOUND_TYPE);
		for (int i = 0; i < list.size(); i++) {
			NbtCompound stackTag = list.getCompound(i);
			setStack(stackTag.getByte("Slot"), ItemStack.fromNbt(stackTag));
		}
	}

	private void writeToParentNBT() {
		NbtCompound tag = parent.getNbt();
		if (tag == null) {
			tag = new NbtCompound();
			parent.setNbt(tag);
		}

		NbtList list = new NbtList();
		for (byte i = 0; i < size(); i++) {
			ItemStack stack = getStack(i);
			if (!stack.isEmpty()) {
				NbtCompound stackTag = new NbtCompound();
				stackTag.putByte("Slot", i);
				stack.writeNbt(stackTag);
				list.add(stackTag);
			}
		}
		tag.put("Items", list);
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : inventory)
			if (!stack.isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return slot >= 0 && slot < size() ? inventory.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int index, int count) {
		ItemStack stack = Inventories.splitStack(inventory, index, count);
		return stack;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack stack = getStack(slot);
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		inventory.set(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > getMaxCountPerStack())
			stack.setCount(getMaxCountPerStack());
		markDirty();
	}

	@Override
	public int getMaxCountPerStack() {
		return 64;
	}

	@Override
	public void markDirty() {
		writeToParentNBT();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public boolean isValid(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public void clear() {
		inventory.clear();
	}
}
