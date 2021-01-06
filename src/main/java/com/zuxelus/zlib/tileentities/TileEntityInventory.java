package com.zuxelus.zlib.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public abstract class TileEntityInventory extends TileEntityFacing implements ISidedInventory {
	protected ItemStack[] inventory;
	protected String customName;

	public TileEntityInventory(String name) {
		customName = name;
		inventory = new ItemStack[getSizeInventory()];
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		NBTTagList nbttaglist = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		inventory = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound stackTag = nbttaglist.getCompoundTagAt(i);
			inventory[stackTag.getByte("Slot")] = ItemStack.loadItemStackFromNBT(stackTag);
		}
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);

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
		return tag;
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
		//if (!stack.isEmpty()) markDirty();
		return stack;
	}

	private static ItemStack getAndSplit(ItemStack[] stacks, int slot, int amount) {
		if (slot >= 0 && slot < stacks.length && stacks[slot] != null && amount > 0) {
			ItemStack stack = stacks[slot].splitStack(amount);
			if (stacks[slot].stackSize == 0)
				stacks[slot] = null;
			return stack;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		ItemStack stack = getStackInSlot(index);
		if (stack == null)
			return null;
		inventory[index] = null;
		return stack;
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
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() { }

	@Override
	public void closeInventory() { }

	public List<ItemStack> getDrops(int fortune) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if (stack != null)
				list.add(stack);
		}
		return list;
	}

	public void dropItems(World world, int x, int y, int z) {
		Random rand = new Random();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack item = getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz,
						new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));

				if (item.hasTagCompound())
					entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());

				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
				item.stackSize = 0;
			}
		}
	}

	// ISidedInventory
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return null;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return false;
	}
}
