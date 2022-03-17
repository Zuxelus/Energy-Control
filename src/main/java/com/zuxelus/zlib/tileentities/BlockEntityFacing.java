package com.zuxelus.zlib.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityFacing extends BlockEntity {

	public BlockEntityFacing(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected Direction facing;
	protected Direction rotation;

	public Direction getFacing() {
		return facing;
	}

	public void setFacing(int meta) {
		facing = Direction.from3DDataValue(meta);
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
		rotation = Direction.from3DDataValue(meta);
	}

	public void setRotation(Direction meta) {
		rotation = meta;
	}

	protected void readProperties(CompoundTag tag) {
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

	protected CompoundTag writeProperties(CompoundTag tag) {
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
