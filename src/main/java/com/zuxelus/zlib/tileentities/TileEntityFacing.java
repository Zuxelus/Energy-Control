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
		facing = Direction.from3DDataValue(meta);
	}

	protected boolean hasRotation() {
		return false;
	}

	public Direction getRotation() {
		return rotation;
	}

	public void setRotation(int meta) {
		rotation = Direction.from3DDataValue(meta);
	}

	public void setRotation(Direction meta) {
		rotation = meta;
	}

	protected void readProperties(CompoundNBT tag) {
		if (tag.contains("facing"))
			facing = Direction.from3DDataValue(tag.getInt("facing"));
		else
			facing = Direction.NORTH;
		if (hasRotation()) {
			if (tag.contains("rotation"))
				rotation = Direction.from3DDataValue(tag.getInt("rotation"));
			else
				rotation = Direction.NORTH;
		}
	}

	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag.putInt("facing", facing.get3DDataValue());
		if (hasRotation() && rotation != null)
			tag.putInt("rotation", rotation.get3DDataValue());
		return tag;
	}

	protected void notifyBlockUpdate() {
		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 2);
	}
}
