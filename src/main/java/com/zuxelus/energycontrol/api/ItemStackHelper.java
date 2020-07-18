package com.zuxelus.energycontrol.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public final class ItemStackHelper {

	public static NBTTagCompound getTagCompound(ItemStack stack) {
		NBTTagCompound nbtTagCompound = stack.getTagCompound();
		if (nbtTagCompound == null) {
			nbtTagCompound = new NBTTagCompound();
			stack.setTagCompound(nbtTagCompound);
		}
		return nbtTagCompound;
	}

	public static void setCoordinates(ItemStack stack, BlockPos pos) {
		NBTTagCompound nbtTagCompound = getTagCompound(stack);
		nbtTagCompound.setInteger("x", pos.getX());
		nbtTagCompound.setInteger("y", pos.getY());
		nbtTagCompound.setInteger("z", pos.getZ());
	}

	public static NBTTagCompound getOrCreateNbtData(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		return tag;
	}

	public static ItemStack getStackWithEnergy(Item item, String name, double energy) {
		ItemStack stack = new ItemStack(item);
		NBTTagCompound tag = new NBTTagCompound();
		stack.setTagCompound(tag);
		tag.setDouble(name, energy);
		return stack;
	}
}
