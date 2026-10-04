package com.zuxelus.zlib.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.ITilePacketHandler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record PacketTileEntityC2S(BlockPos pos, NbtCompound tag) implements PacketBase {
	public static final Id<PacketTileEntityC2S> ID = new Id<>(Identifier.of(EnergyControl.MODID, "c2s_tile"));
	public static final PacketCodec<RegistryByteBuf, PacketTileEntityC2S> CODEC = PacketCodec.tuple(
			BlockPos.PACKET_CODEC, PacketTileEntityC2S::pos,
			PacketCodecs.NBT_COMPOUND, PacketTileEntityC2S::tag,
			PacketTileEntityC2S::new);

	@Override
	public Id<PacketTileEntityC2S> getId() {
		return ID;
	}

	public static void handle(PacketTileEntityC2S packet, ServerPlayNetworking.Context context) {
		ServerPlayerEntity player = context.player();
		context.server().execute(() -> {
			if (player == null || player.getWorld() == null)
				return;
			BlockEntity te = player.getWorld().getBlockEntity(packet.pos());
			if (!(te instanceof ITilePacketHandler))
				return;
			((ITilePacketHandler) te).onServerMessageReceived(packet.tag());
		});
	}
}
