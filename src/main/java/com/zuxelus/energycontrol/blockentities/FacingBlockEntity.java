package com.zuxelus.energycontrol.blockentities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

public class FacingBlockEntity {
	protected Direction facing;
	protected Direction rotation;
	boolean hasRotation;

	public FacingBlockEntity(boolean hasRotation) {
		this.hasRotation = hasRotation;
	}

	public Direction getFacing() {
		return facing;
	}

	public void setFacing(int meta) {
		facing = Direction.byId(meta);
	}

	public void setFacing(Direction meta) {
		facing = meta;
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

	protected void readProperties(CompoundTag tag) {
		if (tag.contains("facing"))
			facing = Direction.byId(tag.getInt("facing"));
		else
			facing = Direction.NORTH;
		if (hasRotation) {
			if (tag.contains("rotation"))
				rotation = Direction.byId(tag.getInt("rotation"));
			else
				rotation = Direction.NORTH;
		}
	}

	protected CompoundTag writeProperties(CompoundTag tag) {
		tag.putInt("facing", facing.getId());
		if (hasRotation && rotation != null)
			tag.putInt("rotation", rotation.getId());
		return tag;
	}
}
