package com.zuxelus.zlib.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

public abstract class TileEntityInventory extends BlockEntityFacing implements WorldlyContainer {
	protected NonNullList<ItemStack> inventory;

	public TileEntityInventory(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);
	}

	@Override
	protected void readProperties(ValueInput tag) {
		super.readProperties(tag);
		inventory = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, inventory);
	}

	@Override
	protected void writeProperties(ValueOutput tag) {
		super.writeProperties(tag);
		ContainerHelper.saveAllItems(tag, inventory);
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
		ItemStack stack = ContainerHelper.removeItem(inventory, index, count);
		if (!stack.isEmpty())
			setChanged();
		return stack;
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
	public boolean stillValid(Player player) {
		return level.getBlockEntity(worldPosition) != this ? false : player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clearContent() {
		inventory.clear();
	}

	public ResourceHandler<ItemResource> getItemHandler(Direction side) {
		return side == null ? VanillaContainerWrapper.of(this) : new WorldlyContainerWrapper(this, side);
	}

	// ISidedInventory
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		return false;
	}
}
