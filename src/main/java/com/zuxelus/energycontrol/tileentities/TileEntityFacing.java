package com.zuxelus.energycontrol.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityFacing extends TileEntity {
	protected ForgeDirection facing;
	protected ForgeDirection rotation;

	public ForgeDirection getFacingForge() {
		return facing;
	}

	public void setFacing(int meta) {
		ForgeDirection newFacing = ForgeDirection.getOrientation(meta);
		if (worldObj != null && !worldObj.isRemote && newFacing != facing)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		facing = ForgeDirection.getOrientation(meta);
	}

	public void setFacing(ForgeDirection meta) {
		facing = meta;
	}

	protected boolean hasRotation() {
		return false;
	}

	public ForgeDirection getRotation() {
		return rotation;
	}

	public void setRotation(int meta) {
		rotation = ForgeDirection.getOrientation(meta);
	}

	public void setRotation(ForgeDirection meta) {
		rotation = meta;
	}

	protected void readProperties(NBTTagCompound tag) {
		ForgeDirection oldFacing = facing;
		if (tag.hasKey("facing"))
			facing = ForgeDirection.getOrientation(tag.getInteger("facing"));
		else
			facing = ForgeDirection.NORTH;
		if (worldObj != null && worldObj.isRemote && oldFacing != facing)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		if (hasRotation()) {
			if (tag.hasKey("rotation"))
				rotation = ForgeDirection.getOrientation(tag.getInteger("rotation"));
			else
				rotation = ForgeDirection.NORTH;
		}
	}

	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag.setInteger("facing", facing.ordinal());
		if (hasRotation() && rotation != null)
			tag.setInteger("rotation", rotation.ordinal());
		return tag;
	}
}
