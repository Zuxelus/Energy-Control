package com.zuxelus.energycontrol.blockentities;

import com.zuxelus.energycontrol.screen.handlers.ISlotItemFilter;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

public abstract class InventoryBlockEntity extends LockableContainerBlockEntity implements ISlotItemFilter {
	private int size;
	private DefaultedList<ItemStack> inventory;
	protected String customName;

	public InventoryBlockEntity(BlockEntityType<?> type, String name, int size) {
		super(type);
		customName = name;
		this.size = size;
		inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
	}

	protected void readProperties(CompoundTag tag) {
		//super.readProperties(tag);
		Inventories.fromTag(tag, inventory);
	}

	protected CompoundTag writeProperties(CompoundTag tag) {
		//tag = super.writeProperties(tag);
		Inventories.toTag(tag, inventory);
		return tag;
	}

	// Inventory
	@Override
	protected Text getContainerName() {
		return new TranslatableText(customName);
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return inventory.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getStack(int slot) {
		return inventory.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack stack = Inventories.splitStack(inventory, slot, amount);
		if (!stack.isEmpty())
			markDirty();
		return stack;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(inventory, slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		if (stack.getCount() > getMaxCountPerStack())
			stack.setCount(getMaxCountPerStack());
		//markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

}
