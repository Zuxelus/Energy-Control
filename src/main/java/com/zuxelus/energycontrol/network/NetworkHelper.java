package com.zuxelus.energycontrol.network;

import com.zuxelus.zlib.network.PacketTileEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.network.PacketDistributor;

public class NetworkHelper {

	public static void sendToPlayer(ServerPlayer player, CustomPacketPayload message) {
		PacketDistributor.sendToPlayer(player, message);
	}

	// client
	public static void sendToServer(CustomPacketPayload message) {
		PacketDistributor.sendToServer(message);
	}

	// server
	public static void sendPacketToAllAround(Level world, BlockPos pos, CustomPacketPayload message) {
		if (world instanceof ServerLevel level)
			PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(pos), message);
	}

	// server
	public static void updateClientTileEntity(ServerPlayer crafter, BlockPos pos, int type, int value) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", type);
		tag.putInt("value", value);
		sendToPlayer(crafter, new PacketTileEntity(pos, tag));
	}

	public static void updateClientTileEntity(ServerPlayer crafter, BlockPos pos, int type, double value) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", type);
		tag.putDouble("value", value);
		sendToPlayer(crafter, new PacketTileEntity(pos, tag));
	}

	public static void updateClientTileEntity(ServerPlayer crafter, BlockPos pos, CompoundTag tag) {
		sendToPlayer(crafter, new PacketTileEntity(pos, tag));
	}

	public static void updateClientTileEntity(Level world, BlockPos pos, CompoundTag tag) {
		sendPacketToAllAround(world, pos, new PacketTileEntity(pos, tag));
	}

	// client
	public static void updateSeverTileEntity(BlockPos pos, int type, String string) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", type);
		tag.putString("string", string);
		sendToServer(new PacketTileEntity(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, int type, int value) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", type);
		tag.putInt("value", value);
		sendToServer(new PacketTileEntity(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, CompoundTag tag) {
		sendToServer(new PacketTileEntity(pos, tag));
	}
}
