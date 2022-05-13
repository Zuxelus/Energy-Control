package com.zuxelus.zlib.items;

import com.zuxelus.zlib.containers.slots.ISlotItemFilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public abstract class ItemInventory implements IInventory, ISlotItemFilter {

	private final ItemStack parent;
	protected ItemStack[] inventory;
	private String customName;

	public ItemInventory(ItemStack parent, String name) {
		this.parent = parent;
		customName = name;
		inventory = new ItemStack[getSizeInventory()];
		readFromParentNBT();
	}

	private void readFromParentNBT() {
		NBTTagCompound tag = parent.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			parent.setTagCompound(tag);
		}

		NBTTagList nbttaglist = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound stackTag = nbttaglist.getCompoundTagAt(i);
			setInventorySlotContents(stackTag.getByte("Slot"), ItemStack.loadItemStackFromNBT(stackTag));
		}
	}

	public void writeToParentNBT(EntityPlayer player) { // player in 1.7.10
		if (player.getHeldItem() == null)
			return;

		NBTTagCompound tag = player.getHeldItem().getTagCompound(); // 1.7.10
		if (tag == null) {
			tag = new NBTTagCompound();
			parent.setTagCompound(tag);
		}

		NBTTagList list = new NBTTagList();
		for (byte i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if (stack != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", i);
				stack.writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
		}
		tag.setTag("Items", list);
	}

	@Override
	public String getInventoryName() {
		return customName;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot >= 0 && slot < getSizeInventory() ? inventory[slot] : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int count) {
		ItemStack stack = getAndSplit(inventory, slot, count);
		//if (stack != null) markDirty();
		return stack;
	}

	private static ItemStack getAndSplit(ItemStack[] stacks, int slot, int amount) { // 1.7.10
		if (slot >= 0 && slot < stacks.length && stacks[slot] != null && amount > 0) {
			ItemStack stack = stacks[slot].splitStack(amount);
			if (stacks[slot].stackSize == 0)
				stacks[slot] = null;
			return stack;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null)
			return null;
		inventory[slot] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() { }

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() { }

	@Override
	public void closeInventory() { }

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}
}
