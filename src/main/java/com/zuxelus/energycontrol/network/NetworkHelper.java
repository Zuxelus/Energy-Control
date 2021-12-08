package com.zuxelus.energycontrol.network;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.zuxelus.zlib.network.PacketTileEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.PacketDistributor.PacketTarget;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class NetworkHelper {
	public static SimpleChannel network;

	public static void createChannel(String name, String version) {
		network = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(name, "main"))
				.clientAcceptedVersions(version::equals).serverAcceptedVersions(version::equals)
				.networkProtocolVersion(() -> version).simpleChannel();
	}

	public static <MSG> void registerClientToServer(int index, Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<Context>> consumer) {
		network.registerMessage(index, type, encoder, decoder, consumer, Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}

	public static <MSG> void registerServerToClient(int index, Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<Context>> consumer) {
		network.registerMessage(index, type, encoder, decoder, consumer, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	public static <MSG> void registerBoth(int index, Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<Context>> consumer) {
		network.registerMessage(index, type, encoder, decoder, consumer, Optional.empty());
	}

	public static <MSG> void sendToPlayer(ServerPlayer player, MSG message) {
		network.sendTo(message, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
	}

	// server
	public static <MSG> void sendPacketToAllAround(Level world, BlockPos pos, MSG message) {
		LevelChunk chunk = world.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
		PacketTarget target = PacketDistributor.TRACKING_CHUNK.with(() -> chunk);
		network.send(target, message);
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
		network.sendToServer(new PacketTileEntity(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, int type, int value) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("type", type);
		tag.putInt("value", value);
		network.sendToServer(new PacketTileEntity(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, CompoundTag tag) {
		network.sendToServer(new PacketTileEntity(pos, tag));
	}
}
