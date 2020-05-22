package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.containers.ISlotItemFilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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

	private void writeToParentNBT() {
		NBTTagCompound tag = parent.getTagCompound();
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
	public ItemStack getStackInSlot(int index) {
		return index >= 0 && index < getSizeInventory() ? inventory[index] : null;
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
		if (itemstack == null)
			return null;
		inventory[index] = null;
		return itemstack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inventory[index] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();
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
	public boolean isUseableByPlayer(EntityPlayer player) {
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
		for (int i = 0; i < getSizeInventory(); ++i)
			inventory[i] = null;
	}
}
