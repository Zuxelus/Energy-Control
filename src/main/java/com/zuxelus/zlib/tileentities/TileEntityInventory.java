package com.zuxelus.zlib.tileentities;

import java.util.HashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;

public abstract class TileEntityInventory extends BlockEntityFacing implements WorldlyContainer {
	protected NonNullList<ItemStack> inventory;
	private HashMap<Direction, IItemHandler> itemHandlers = new HashMap<>();

	public TileEntityInventory(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);
	}

	@Override
	protected void readProperties(CompoundTag tag) {
		super.readProperties(tag);
		inventory = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, inventory);
	}

	@Override
	protected CompoundTag writeProperties(CompoundTag tag) {
		tag = super.writeProperties(tag);
		ContainerHelper.saveAllItems(tag, inventory);
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
	public @NotNull ItemStack getItem(int slot) {
		return slot >= 0 && slot < getContainerSize() ? inventory.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public @NotNull ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(inventory, index, count);
	}

	@Override
	public @NotNull ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = getItem(slot);
		if (stack.isEmpty())
			return ItemStack.EMPTY;
		inventory.set(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int slot, @NotNull ItemStack stack) {
		inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack.setCount(getMaxStackSize());
		setChanged();
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return level.getBlockEntity(worldPosition) != this ? false : player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clearContent() {
		inventory.clear();
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			IItemHandler handler = itemHandlers.get(side);
			if (handler == null) {
				handler = new SidedInvWrapper(this, side);
				itemHandlers.put(side, handler);
			}
			return LazyOptional.of(() -> itemHandlers.get(side)).cast();
		}

		return super.getCapability(cap, side);
	}

	// ISidedInventory
	@Override
	public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
		return new int[0];
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, Direction side) {
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction side) {
		return false;
	}
}
