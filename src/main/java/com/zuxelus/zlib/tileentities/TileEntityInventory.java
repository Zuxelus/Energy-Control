package com.zuxelus.zlib.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public abstract class TileEntityInventory extends TileEntityFacing implements IInventory {
	protected NonNullList<ItemStack> inventory;

	public TileEntityInventory(TileEntityType<?> type) {
		super(type);
		inventory = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);
	}

	@Override
	protected void readProperties(CompoundNBT tag) {
		super.readProperties(tag);
		ListNBT list = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
		inventory = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT stackTag = list.getCompound(i);
			inventory.set(stackTag.getByte("Slot"), ItemStack.of(stackTag));
		}
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag = super.writeProperties(tag);

		ListNBT list = new ListNBT();
		for (byte i = 0; i < getContainerSize(); i++) {
			ItemStack stack = getItem(i);
			if (!stack.isEmpty()) {
				CompoundNBT stackTag = new CompoundNBT();
				stackTag.putByte("Slot", i);
				stack.save(stackTag);
				list.add(stackTag);
			}
		}
		tag.put("Items", list);
		return tag;
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
		ItemStack itemstack = ItemStackHelper.removeItem(inventory, index, count);
		//if (!itemstack.isEmpty()) markDirty();
		return itemstack;
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
	public boolean stillValid(PlayerEntity player) {
		return level.getBlockEntity(worldPosition) != this ? false : player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clearContent() {
		inventory.clear();
	}

	public List<ItemStack> getDrops(int fortune) {
		List<ItemStack> list = new ArrayList<>();
		for (int i = 0; i < getContainerSize(); i++) {
			ItemStack stack = getItem(i);
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

			ItemEntity entityItem = new ItemEntity(world, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
					new ItemStack(stack.getItem(), stack.getCount()));

			if (stack.hasTag())
				entityItem.getItem().setTag(stack.getTag().copy());

			float factor = 0.05F;
			entityItem.setDeltaMovement(rand.nextGaussian() * factor, rand.nextGaussian() * factor + 0.2F, rand.nextGaussian() * factor);
			world.addFreshEntity(entityItem);
			stack.setCount(0);
		}
	}
}
