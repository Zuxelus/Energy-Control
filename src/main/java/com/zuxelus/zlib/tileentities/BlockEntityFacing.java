package com.zuxelus.zlib.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.nbt.CompoundTag;


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

	protected void readProperties(CompoundTag tag, HolderLookup.Provider registries) {
		facing = Direction.from3DDataValue((tag.contains("facing") ? tag.getInt("facing") : Direction.NORTH.get3DDataValue()));
		if (hasRotation())
			rotation = Direction.from3DDataValue((tag.contains("rotation") ? tag.getInt("rotation") : Direction.NORTH.get3DDataValue()));
	}

	protected void writeProperties(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putInt("facing", facing == null ? Direction.NORTH.get3DDataValue() : facing.get3DDataValue());
		if (hasRotation() && rotation != null)
			tag.putInt("rotation", rotation.get3DDataValue());
	}

	/**
	 * Extra data sent to the client only (update tag / data packet).
	 */
	protected void writeUpdateData(CompoundTag tag) { }

	@Override
	protected void loadAdditional(CompoundTag input, HolderLookup.Provider registries) {
		super.loadAdditional(input, registries);
		readProperties(input, registries);
	}

	@Override
	protected void saveAdditional(CompoundTag output, HolderLookup.Provider registries) {
		super.saveAdditional(output, registries);
		writeProperties(output, registries);
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
		CompoundTag output = new CompoundTag();
		writeProperties(output, registries);
		writeUpdateData(output);
		return output;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
		readProperties(packet.getTag(), registries);
	}

	@Override
	public void handleUpdateTag(CompoundTag input, HolderLookup.Provider registries) {
		readProperties(input, registries);
	}

	protected void notifyBlockUpdate() {
		if (!level.isClientSide) {
			BlockState state = level.getBlockState(worldPosition);
			level.sendBlockUpdated(worldPosition, state, state, 2);
		}
	}
}
