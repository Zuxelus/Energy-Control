package com.zuxelus.zlib.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public abstract class TileEntityFacing extends TileEntity {
	protected EnumFacing facing;
	protected EnumFacing rotation;

	public EnumFacing getFacing() {
		return facing;
	}

	public void setFacing(int meta) {
		facing = EnumFacing.getFront(meta);
	}

	public void setFacing(EnumFacing meta) {
		facing = meta;
	}

	protected boolean hasRotation() {
		return false;
	}

	public EnumFacing getRotation() {
		return rotation;
	}

	public void setRotation(int meta) {
		rotation = EnumFacing.getFront(meta);
	}

	public void setRotation(EnumFacing meta) {
		rotation = meta;
	}

	protected void readProperties(NBTTagCompound tag) {
		if (tag.hasKey("facing"))
			facing = EnumFacing.getFront(tag.getInteger("facing"));
		else
			facing = EnumFacing.NORTH;
		if (hasRotation()) {
			if (tag.hasKey("rotation"))
				rotation = EnumFacing.getFront(tag.getInteger("rotation"));
			else
				rotation = EnumFacing.NORTH;
		}
	}

	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag.setInteger("facing", facing.getIndex());
		if (hasRotation() && rotation != null)
			tag.setInteger("rotation", rotation.getIndex());
		return tag;
	}

	protected void notifyBlockUpdate() {
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 2);
	}
}
