package com.zuxelus.zlib.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.ITilePacketHandler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public record PacketTileEntityC2S(BlockPos pos, CompoundTag tag) implements CustomPacketPayload {
	public static final Type<PacketTileEntityC2S> ID = new Type<>(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "c2s_tile"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketTileEntityC2S> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, PacketTileEntityC2S::pos,
			ByteBufCodecs.COMPOUND_TAG, PacketTileEntityC2S::tag,
			PacketTileEntityC2S::new);

	@Override
	public Type<PacketTileEntityC2S> type() {
		return ID;
	}

	public static void handle(PacketTileEntityC2S packet, ServerPlayNetworking.Context context) {
		ServerPlayer player = context.player();
		context.server().execute(() -> {
			if (player == null || player.level() == null || !player.level().isLoaded(packet.pos()))
				return;
			BlockEntity te = player.level().getBlockEntity(packet.pos());
			if (!(te instanceof ITilePacketHandler))
				return;
			((ITilePacketHandler) te).onServerMessageReceived(packet.tag());
		});
	}
}
