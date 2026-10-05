package com.zuxelus.zlib.tileentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public abstract class BlockEntityFacing extends BlockEntity {

	public BlockEntityFacing(BlockEntityType<?> type) {
		super(type);
	}

	protected Direction facing;
	protected Direction rotation;

	public Direction getFacing() {
		// 1.16 creates block entities without a state, so blocks not placed by a player (/setblock, structures) start without a facing
		if (facing == null && world != null) {
			BlockState state = getCachedState();
			facing = state.contains(Properties.FACING) ? state.get(Properties.FACING) : state.contains(Properties.HORIZONTAL_FACING) ? state.get(Properties.HORIZONTAL_FACING) : Direction.NORTH;
		}
		return facing;
	}

	public void setFacing(int meta) {
		facing = Direction.byId(meta);
	}

	protected boolean hasRotation() {
		return false;
	}

	public Direction getRotation() {
		return rotation;
	}

	public void setRotation(int meta) {
		rotation = Direction.byId(meta);
	}

	public void setRotation(Direction meta) {
		rotation = meta;
	}

	protected void readProperties(NbtCompound tag) {
		if (tag.contains("facing"))
			facing = Direction.byId(tag.getInt("facing"));
		else
			facing = Direction.NORTH;
		if (hasRotation()) {
			if (tag.contains("rotation"))
				rotation = Direction.byId(tag.getInt("rotation"));
			else
				rotation = Direction.NORTH;
		}
	}

	protected NbtCompound writeProperties(NbtCompound tag) {
		tag.putInt("facing", getFacing() == null ? Direction.NORTH.getId() : facing.getId());
		if (hasRotation() && rotation != null)
			tag.putInt("rotation", rotation.getId());
		return tag;
	}

	protected void notifyBlockUpdate() {
		BlockState state = world.getBlockState(pos);
		world.updateListeners(pos, state, state, 2);
	}
}
