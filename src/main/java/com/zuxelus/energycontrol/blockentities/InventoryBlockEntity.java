package com.zuxelus.energycontrol.blockentities;

import com.zuxelus.energycontrol.containers.ISlotItemFilter;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;

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
	public int getInvSize() {
		return size;
	}

	@Override
	public boolean isInvEmpty() {
		return inventory.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return inventory.get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack stack = Inventories.splitStack(inventory, slot, amount);
		if (!stack.isEmpty())
			markDirty();
		return stack;
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return Inventories.removeStack(inventory, slot);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		if (stack.getCount() > getInvMaxStackAmount())
			stack.setCount(getInvMaxStackAmount());
		//markDirty();
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return true;
	}

}
