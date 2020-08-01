package com.zuxelus.energycontrol.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public final class ItemStackHelper {

	public static CompoundTag getTagCompound(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			tag = new CompoundTag();
			stack.setTag(tag);
		}
		return tag;
	}

	public static void setCoordinates(ItemStack stack, BlockPos pos) {
		CompoundTag tag = getTagCompound(stack);
		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());
	}

	public static CompoundTag getOrCreateNbtData(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			tag = new CompoundTag();
			stack.setTag(tag);
		}
		return tag;
	}

	public static ItemStack getStackWithEnergy(Item item, double energy) {
		ItemStack stack = new ItemStack(item);
		CompoundTag tag = new CompoundTag();
		stack.setTag(tag);
		tag.putDouble("charge", energy);
		return stack;
	}
}
