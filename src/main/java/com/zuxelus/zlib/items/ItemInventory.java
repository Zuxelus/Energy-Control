package com.zuxelus.zlib.items;

import com.zuxelus.zlib.containers.slots.ISlotItemFilter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

public abstract class ItemInventory implements IInventory, ISlotItemFilter {

	private final ItemStack parent;
	protected NonNullList<ItemStack> inventory;

	public ItemInventory(ItemStack parent) {
		this.parent = parent;
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
		readFromParentNBT();
	}

	private void readFromParentNBT() {
		CompoundNBT tag = parent.getTag();
		if (tag == null) {
			tag = new CompoundNBT();
			parent.setTag(tag);
		}

		ListNBT list = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT stackTag = list.getCompound(i);
			setInventorySlotContents(stackTag.getByte("Slot"), ItemStack.read(stackTag));
		}
	}

	private void writeToParentNBT() {
		CompoundNBT tag = parent.getTag();
		if (tag == null) {
			tag = new CompoundNBT();
			parent.setTag(tag);
		}

		ListNBT list = new ListNBT();
		for (byte i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if (!stack.isEmpty()) {
				CompoundNBT stackTag = new CompoundNBT();
				stackTag.putByte("Slot", i);
				stack.write(stackTag);
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
	public ItemStack getStackInSlot(int slot) {
		return slot >= 0 && slot < getSizeInventory() ? inventory.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = ItemStackHelper.getAndSplit(inventory, index, count);
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		inventory.set(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		writeToParentNBT();
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public void clear() {
		inventory.clear();
	}
}
