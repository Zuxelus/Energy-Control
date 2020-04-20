package com.zuxelus.energycontrol.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class ItemStackHelper {
	public static NBTTagCompound getTagCompound(ItemStack stack) {
		NBTTagCompound nbtTagCompound = stack.getTagCompound();
		if (nbtTagCompound == null) {
			nbtTagCompound = new NBTTagCompound();
			stack.setTagCompound(nbtTagCompound);
		}
		return nbtTagCompound;
	}
	
	/*public static void setTag(ItemStack stack, String name, NBTTagCompound value) {
		NBTTagCompound nbtTagCompound = getTagCompound(stack);
		if (value == null) {
			nbtTagCompound.removeTag(name);
		} else
			nbtTagCompound.setTag(name, value);
	}

	public static NBTTagCompound getTag(ItemStack stack, String name) {
		NBTTagCompound nbtTagCompound = stack.getTagCompound();
		if (nbtTagCompound == null)
			return null;
		return (NBTTagCompound) nbtTagCompound.getTag(name);
	}

	public static void clearField(ItemStack stack, String name) {
		NBTTagCompound nbtTagCompound = stack.getTagCompound();
		if (nbtTagCompound != null)
			nbtTagCompound.removeTag(name);
	}
	
	public static ChunkCoordinates getTarget(ItemStack stack) {
		NBTTagCompound nbtTagCompound = stack.getTagCompound();
		if (nbtTagCompound == null)
			return null;
		
		ChunkCoordinates coordinates = new ChunkCoordinates();
		coordinates.posX = nbtTagCompound.getInteger("x");
		coordinates.posY = nbtTagCompound.getInteger("y");
		coordinates.posZ = nbtTagCompound.getInteger("z");
		return coordinates;
	}*/
	
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
