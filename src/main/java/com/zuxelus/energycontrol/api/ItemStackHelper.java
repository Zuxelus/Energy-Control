package com.zuxelus.energycontrol.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class ItemStackHelper {

	public static NBTTagCompound getTagCompound(ItemStack stack) {
		NBTTagCompound nbtTagCompound = stack.getTagCompound();
		if (nbtTagCompound == null) {
			nbtTagCompound = new NBTTagCompound();
			stack.setTagCompound(nbtTagCompound);
		}
		return nbtTagCompound;
	}

	public static void setCoordinates(ItemStack stack, int x, int y, int z) {
		NBTTagCompound nbtTagCompound = getTagCompound(stack);
		nbtTagCompound.setInteger("x", x);
		nbtTagCompound.setInteger("y", y);
		nbtTagCompound.setInteger("z", z);
	}

	public static ItemStack getAndSplit(ItemStack[] stacks, int index, int amount) {
		if (index >= 0 && index < stacks.length && stacks[index] != null && amount > 0) {
			ItemStack itemstack = stacks[index].splitStack(amount);
			if (stacks[index].stackSize == 0)
				stacks[index] = null;
			return itemstack;
		}
		return null;
	}
}
