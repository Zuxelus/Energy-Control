package com.zuxelus.zlib.network;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHelper {
	public static SimpleChannel network;

	public static void createChannel(String name, String version) {
		network = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(name, "main"))
				.clientAcceptedVersions(version::equals).serverAcceptedVersions(version::equals)
				.networkProtocolVersion(() -> version).simpleChannel();
	}

	public static <MSG> void registerClientToServer(int index, Class<MSG> type, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<Context>> consumer) {
		network.registerMessage(index, type, encoder, decoder, consumer, Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}

	public static <MSG> void registerServerToClient(int index, Class<MSG> type, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<Context>> consumer) {
		network.registerMessage(index, type, encoder, decoder, consumer, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	public static <MSG> void registerBoth(int index, Class<MSG> type, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<Context>> consumer) {
		network.registerMessage(index, type, encoder, decoder, consumer, Optional.empty());
	}

	public static <MSG> void sendToPlayer(ServerPlayerEntity player, MSG message) {
		network.sendTo(message, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
	}

	// server
	public static <MSG> void sendPacketToAllAround(World world, BlockPos pos, MSG message) {
		Chunk chunk = world.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
		PacketTarget target = PacketDistributor.TRACKING_CHUNK.with(() -> chunk);
		network.send(target, message);
	}

	// server
	public static void updateClientTileEntity(IContainerListener crafter, BlockPos pos, int type, int value) {
		if (!(crafter instanceof ServerPlayerEntity))
			return;
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("type", type);
		tag.putInt("value", value);
		sendToPlayer((ServerPlayerEntity) crafter, new PacketTileEntity(pos, tag));
	}

	public static void updateClientTileEntity(IContainerListener crafter, BlockPos pos, int type, double value) {
		if (!(crafter instanceof ServerPlayerEntity))
			return;
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("type", type);
		tag.putDouble("value", value);
		sendToPlayer((ServerPlayerEntity) crafter, new PacketTileEntity(pos, tag));
	}

	public static void updateClientTileEntity(IContainerListener crafter, BlockPos pos, CompoundNBT tag) {
		if (!(crafter instanceof ServerPlayerEntity))
			return;
		sendToPlayer((ServerPlayerEntity) crafter, new PacketTileEntity(pos, tag));
	}

	public static void updateClientTileEntity(World world, BlockPos pos, CompoundNBT tag) {
		sendPacketToAllAround(world, pos, new PacketTileEntity(pos, tag));
	}

	// client
	public static void updateSeverTileEntity(BlockPos pos, int type, String string) {
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("type", type);
		tag.putString("string", string);
		network.sendToServer(new PacketTileEntity(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, int type, int value) {
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("type", type);
		tag.putInt("value", value);
		network.sendToServer(new PacketTileEntity(pos, tag));
	}

	public static void updateSeverTileEntity(BlockPos pos, CompoundNBT tag) {
		network.sendToServer(new PacketTileEntity(pos, tag));
	}
}
