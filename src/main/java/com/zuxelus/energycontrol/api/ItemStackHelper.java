package com.zuxelus.energycontrol.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class ItemStackHelper {

	public static NBTTagCompound getTagCompound(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		return tag;
	}

	public static void setCoordinates(ItemStack stack, int x, int y, int z) {
		NBTTagCompound tag = getTagCompound(stack);
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
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
