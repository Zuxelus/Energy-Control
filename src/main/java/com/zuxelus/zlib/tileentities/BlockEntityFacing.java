package com.zuxelus.zlib.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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

	protected void readProperties(ValueInput tag) {
		facing = Direction.from3DDataValue(tag.getIntOr("facing", Direction.NORTH.get3DDataValue()));
		if (hasRotation())
			rotation = Direction.from3DDataValue(tag.getIntOr("rotation", Direction.NORTH.get3DDataValue()));
	}

	protected void writeProperties(ValueOutput tag) {
		tag.putInt("facing", facing == null ? Direction.NORTH.get3DDataValue() : facing.get3DDataValue());
		if (hasRotation() && rotation != null)
			tag.putInt("rotation", rotation.get3DDataValue());
	}

	/**
	 * Extra data sent to the client only (update tag / data packet).
	 */
	protected void writeUpdateData(ValueOutput tag) { }

	// also called on the client for the update tag and data packet
	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		readProperties(input);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		writeProperties(output);
	}

	protected boolean sendUpdatePacket() {
		return true;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return sendUpdatePacket() ? ClientboundBlockEntityDataPacket.create(this) : null;
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
		writeProperties(output);
		writeUpdateData(output);
		return output.buildResult();
	}

	protected void notifyBlockUpdate() {
		if (!level.isClientSide()) {
			BlockState state = level.getBlockState(worldPosition);
			level.sendBlockUpdated(worldPosition, state, state, 2);
		}
	}
}
