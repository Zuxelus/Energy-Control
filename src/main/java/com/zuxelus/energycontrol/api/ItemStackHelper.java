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

	public static void setCoordinates(ItemStack stack, BlockPos pos, int type) {
		NBTTagCompound nbtTagCompound = getTagCompound(stack);
		nbtTagCompound.setInteger("x", pos.getX());
		nbtTagCompound.setInteger("y", pos.getY());
		nbtTagCompound.setInteger("z", pos.getZ());
		nbtTagCompound.setInteger("targetType", type);
	}
}
