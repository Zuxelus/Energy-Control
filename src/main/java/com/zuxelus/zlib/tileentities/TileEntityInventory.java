package com.zuxelus.zlib.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TileEntityInventory extends BlockEntityFacing implements Inventory {
	protected DefaultedList<ItemStack> inventory;

	public TileEntityInventory(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY);
	}

	@Override
	protected void readProperties(NbtCompound tag) {
		super.readProperties(tag);
		inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY);
		Inventories.readNbt(tag, inventory);
	}

	@Override
	protected NbtCompound writeProperties(NbtCompound tag) {
		tag = super.writeProperties(tag);
		Inventories.writeNbt(tag, inventory);
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
	public boolean canPlayerUse(PlayerEntity player) {
		return world.getBlockEntity(pos) != this ? false : player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	public List<ItemStack> getDrops(int fortune) {
		List<ItemStack> list = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			ItemStack stack = getStack(i);
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

			ItemEntity entityItem = new ItemEntity(world, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, new ItemStack(stack.getItem(), stack.getCount()));
			if (stack.hasNbt())
				entityItem.getStack().setNbt(stack.getNbt().copy());

			float factor = 0.05F;
			entityItem.setVelocity(rand.nextGaussian() * factor, rand.nextGaussian() * factor + 0.2F, rand.nextGaussian() * factor);
			world.spawnEntity(entityItem);
			stack.setCount(0);
		}
	}
}
