package com.zuxelus.zlib.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

	protected void readProperties(ValueInput tag) {
		facing = Direction.from3DDataValue(tag.getIntOr("facing", Direction.NORTH.get3DDataValue()));
		if (hasRotation())
			rotation = Direction.from3DDataValue(tag.getIntOr("rotation", Direction.NORTH.get3DDataValue()));
	}

	protected void writeProperties(ValueOutput tag) {
		tag.putInt("facing", facing.get3DDataValue());
		if (hasRotation() && rotation != null)
			tag.putInt("rotation", rotation.get3DDataValue());
	}

	// update packets still carry a raw CompoundTag
	protected void readProperties(CompoundTag tag, HolderLookup.Provider registries) {
		readProperties(TagValueInput.create(ProblemReporter.DISCARDING, registries, tag));
	}

	protected CompoundTag writeProperties(HolderLookup.Provider registries) {
		TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
		writeProperties(output);
		return output.buildResult();
	}

	protected void notifyBlockUpdate() {
		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 2);
	}
}
