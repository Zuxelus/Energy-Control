package com.zuxelus.zlib.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

public abstract class TileEntityFacing extends TileEntity {

	public TileEntityFacing(TileEntityType<?> type) {
		super(type);
	}

	protected Direction facing;
	protected Direction rotation;

	public Direction getFacing() {
		return facing;
	}

	public void setFacing(int meta) {
		facing = Direction.byIndex(meta);
	}

	public void setFacing(Direction meta) {
		facing = meta;
	}

	protected boolean hasRotation() {
		return false;
	}

	public Direction getRotation() {
		return rotation;
	}

	public void setRotation(int meta) {
		rotation = Direction.byIndex(meta);
	}

	public void setRotation(Direction meta) {
		rotation = meta;
	}

	protected void readProperties(CompoundNBT tag) {
		if (tag.contains("facing"))
			facing = Direction.byIndex(tag.getInt("facing"));
		else
			facing = Direction.NORTH;
		if (hasRotation()) {
			if (tag.contains("rotation"))
				rotation = Direction.byIndex(tag.getInt("rotation"));
			else
				rotation = Direction.NORTH;
		}
	}

	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag.putInt("facing", facing.getIndex());
		if (hasRotation() && rotation != null)
			tag.putInt("rotation", rotation.getIndex());
		return tag;
	}

	protected void notifyBlockUpdate() {
		BlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 2);
	}
}
