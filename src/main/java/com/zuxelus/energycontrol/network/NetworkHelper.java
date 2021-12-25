package com.zuxelus.energycontrol.network;

import com.zuxelus.zlib.network.PacketBase;
import com.zuxelus.zlib.network.PacketTileEntityC2S;
import com.zuxelus.zlib.network.PacketTileEntityS2C;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkHelper {

	public static void sendToPlayer(ServerPlayerEntity player, PacketBase packet) {
		ServerPlayNetworking.send(player, packet.getId(), packet);
	}

	// server
	public static void sendPacketToAllAround(ServerWorld world, PacketBase packet) {
		for (ServerPlayerEntity player : world.getPlayers())
			sendToPlayer(player, packet);
	}

	// server
	public static void updateClientTileEntity(ServerPlayerEntity crafter, BlockPos pos, int type, int value) {
		NbtCompound tag = new NbtCompound();
		tag.putInt("type", type);
		tag.putInt("value", value);
		sendToPlayer(crafter, new PacketTileEntityS2C(pos, tag));
	}

	public static void updateClientTileEntity(ServerPlayerEntity crafter, BlockPos pos, int type, double value) {
		NbtCompound tag = new NbtCompound();
		tag.putInt("type", type);
		tag.putDouble("value", value);
		sendToPlayer(crafter, new PacketTileEntityS2C(pos, tag));
	}

	public static void updateClientTileEntity(ServerPlayerEntity crafter, BlockPos pos, NbtCompound tag) {
		sendToPlayer(crafter, new PacketTileEntityS2C(pos, tag));
	}

	public static void updateClientTileEntity(World world, BlockPos pos, NbtCompound tag) {
		sendPacketToAllAround((ServerWorld) world, new PacketTileEntityS2C(pos, tag));
	}

	// client
	public static void sendToServer(PacketBase packet) {
		ClientPlayNetworking.send(packet.getId(), packet);
	}

	public static void updateSeverTileEntity(BlockPos pos, int type, String string) {
		NbtCompound tag = new NbtCompound();
		tag.putInt("type", type);
		tag.putString("string", string);
		sendToServer(new PacketTileEntityC2S(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, int type, int value) {
		NbtCompound tag = new NbtCompound();
		tag.putInt("type", type);
		tag.putInt("value", value);
		sendToServer(new PacketTileEntityC2S(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, NbtCompound tag) {
		sendToServer(new PacketTileEntityC2S(pos, tag));
	}
}
