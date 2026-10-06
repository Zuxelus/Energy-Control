package com.zuxelus.energycontrol.network;

import com.zuxelus.zlib.network.PacketBase;
import com.zuxelus.zlib.network.PacketTileEntityC2S;
import com.zuxelus.zlib.network.PacketTileEntityS2C;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class NetworkHelper {

	public static void sendToPlayer(ServerPlayer player, PacketBase packet) {
		ServerPlayNetworking.send(player, packet);
	}

	// server
	public static void sendPacketToAllAround(ServerLevel world, PacketBase packet) {
		for (ServerPlayer player : world.players())
			sendToPlayer(player, packet);
	}

	// server
	public static void updateClientTileEntity(ServerPlayer crafter, BlockPos pos, int type, int value) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", type);
		tag.putInt("value", value);
		sendToPlayer(crafter, new PacketTileEntityS2C(pos, tag));
	}

	public static void updateClientTileEntity(ServerPlayer crafter, BlockPos pos, int type, double value) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", type);
		tag.putDouble("value", value);
		sendToPlayer(crafter, new PacketTileEntityS2C(pos, tag));
	}

	public static void updateClientTileEntity(ServerPlayer crafter, BlockPos pos, CompoundTag tag) {
		sendToPlayer(crafter, new PacketTileEntityS2C(pos, tag));
	}

	public static void updateClientTileEntity(Level world, BlockPos pos, CompoundTag tag) {
		sendPacketToAllAround((ServerLevel) world, new PacketTileEntityS2C(pos, tag));
	}

	// client
	public static void sendToServer(PacketBase packet) {
		ClientPlayNetworking.send(packet);
	}

	public static void updateSeverTileEntity(BlockPos pos, int type, String string) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", type);
		tag.putString("string", string);
		sendToServer(new PacketTileEntityC2S(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, int type, int value) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", type);
		tag.putInt("value", value);
		sendToServer(new PacketTileEntityC2S(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, CompoundTag tag) {
		sendToServer(new PacketTileEntityC2S(pos, tag));
	}
}
