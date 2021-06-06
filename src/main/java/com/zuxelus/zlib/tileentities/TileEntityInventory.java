package com.zuxelus.zlib.tileentities;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class TileEntityInventory extends TileEntityFacing implements ISidedInventory {
	protected NonNullList<ItemStack> inventory;
	protected String customName;

	public TileEntityInventory(String name) {
		customName = name;
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		NBTTagList list = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			inventory.set(stackTag.getByte("Slot"), new ItemStack(stackTag));
		}
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);

		NBTTagList list = new NBTTagList();
		for (byte i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if (!stack.isEmpty()) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", i);
				stack.writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
		}
		tag.setTag("Items", list);
		return tag;
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
		ItemStack itemstack = ItemStackHelper.getAndSplit(inventory, index, count);
		//if (!itemstack.isEmpty()) markDirty();
		return itemstack;
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
	public boolean isUsableByPlayer(EntityPlayer player) {
		return world.getTileEntity(this.pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) { }

	@Override
	public void closeInventory(EntityPlayer player) { }

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

	public List<ItemStack> getDrops(int fortune) {
		List<ItemStack> list = new ArrayList<>();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if (!stack.isEmpty())
				list.add(stack);
		}
		return list;
	}

	public void dropItems(World world, BlockPos pos) {
		Random rand = new Random();
		List<ItemStack> list = getDrops(1);
		for (ItemStack stack : list) {
			float rx = rand.nextFloat() * 0.8F + 0.1F;
			float ry = rand.nextFloat() * 0.8F + 0.1F;
			float rz = rand.nextFloat() * 0.8F + 0.1F;

			EntityItem entityItem = new EntityItem(world, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
					new ItemStack(stack.getItem(), stack.getCount(), stack.getItemDamage()));

			if (stack.hasTagCompound())
				entityItem.getItem().setTagCompound(stack.getTagCompound().copy());

			float factor = 0.05F;
			entityItem.motionX = rand.nextGaussian() * factor;
			entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
			entityItem.motionZ = rand.nextGaussian() * factor;
			world.spawnEntity(entityItem);
			stack.setCount(0);
		}
	}

	// ISidedInventory
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return null;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
		return false;
	}
}
