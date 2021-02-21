package com.zuxelus.energycontrol.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public final class ItemStackHelper {

	public static CompoundNBT getTagCompound(ItemStack stack) {
		CompoundNBT tag = stack.getTag();
		if (tag == null) {
			tag = new CompoundNBT();
			stack.setTag(tag);
		}
		return tag;
	}

	public static void setCoordinates(ItemStack stack, BlockPos pos) {
		CompoundNBT tag = getTagCompound(stack);
		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());
	}

	public static CompoundNBT getOrCreateNbtData(ItemStack stack) {
		CompoundNBT tag = stack.getTag();
		if (tag == null) {
			tag = new CompoundNBT();
			stack.setTag(tag);
		}
		return tag;
	}

	public static ItemStack getStackWithEnergy(Item item, double energy) {
		ItemStack stack = new ItemStack(item);
		CompoundNBT tag = new CompoundNBT();
		stack.setTag(tag);
		tag.putDouble("charge", energy);
		return stack;
	}
}
