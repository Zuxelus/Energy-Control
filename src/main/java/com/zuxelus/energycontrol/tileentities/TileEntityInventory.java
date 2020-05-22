package com.zuxelus.energycontrol.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public abstract class TileEntityInventory extends TileEntityFacing implements IInventory {
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
	public String getName() {
		return customName;
	}

	@Override
	public boolean hasCustomName() {
		return false;
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
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
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
		for (int i = 0; i < getSizeInventory(); ++i)
			inventory[i] = null;
	}

	public List<ItemStack> getDrops(int fortune) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if (stack != null)
				list.add(stack);
		}
		return list;
	}

	public void dropItems(World world, BlockPos pos) {
		Random rand = new Random();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack item = getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
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
}
