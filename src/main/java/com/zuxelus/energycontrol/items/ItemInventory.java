package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.containers.ISlotItemFilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

public abstract class ItemInventory implements IInventory, ISlotItemFilter {

	private final ItemStack parent;
	protected NonNullList<ItemStack> inventory;
	private String customName;

	public ItemInventory(ItemStack parent, String name) {
		this.parent = parent;
		customName = name;
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
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
			setInventorySlotContents(stackTag.getByte("Slot"), new ItemStack(stackTag));
		}
	}

	private void writeToParentNBT() {
		NBTTagCompound tag = parent.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			parent.setTagCompound(tag);
		}

		NBTTagList list = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if (!stack.isEmpty()) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				stack.writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
		}

		tag.setTag("Items", list);
	}

	@Override
	public String getName() {
		return customName;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(customName);
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : inventory)
			if (!itemstack.isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return index >= 0 && index < getSizeInventory() ? inventory.get(index) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(inventory, index, count);
		//if (!itemstack.isEmpty()) markDirty();
		return itemstack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack itemstack = getStackInSlot(index);
		if (itemstack.isEmpty())
			return ItemStack.EMPTY;
		inventory.set(index, ItemStack.EMPTY);
		return itemstack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inventory.set(index, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit())
			stack.setCount(this.getInventoryStackLimit());
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
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) { }

	@Override
	public void closeInventory(EntityPlayer player) { }

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) { }

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inventory.clear();
	}
}
