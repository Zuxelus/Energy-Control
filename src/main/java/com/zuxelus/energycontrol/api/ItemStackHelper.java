package com.zuxelus.energycontrol.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public final class ItemStackHelper {

	public static NbtCompound getTagCompound(ItemStack stack) {
		NbtCompound tag = stack.getNbt();
		if (tag == null) {
			tag = new NbtCompound();
			stack.setNbt(tag);
		}
		return tag;
	}

	public static void setCoordinates(ItemStack stack, BlockPos pos) {
		NbtCompound tag = getTagCompound(stack);
		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());
	}

	public static NbtCompound getOrCreateNbtData(ItemStack stack) {
		NbtCompound tag = stack.getNbt();
		if (tag == null) {
			tag = new NbtCompound();
			stack.setNbt(tag);
		}
		return tag;
	}

	public static ItemStack getStackWithEnergy(Item item, double energy) {
		ItemStack stack = new ItemStack(item);
		NbtCompound tag = new NbtCompound();
		stack.setNbt(tag);
		tag.putDouble("charge", energy);
		return stack;
	}
}
