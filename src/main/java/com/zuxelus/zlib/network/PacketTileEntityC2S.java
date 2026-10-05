package com.zuxelus.zlib.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.ITilePacketHandler;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PacketTileEntityC2S extends PacketBase {
	public static final Identifier ID = new Identifier(EnergyControl.MODID, "c2s_tile");

	public PacketTileEntityC2S(BlockPos pos, NbtCompound tag) {
		writeBlockPos(pos);
		writeNbt(tag);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		BlockPos pos = buf.readBlockPos();
		NbtCompound tag = buf.readNbt();
		server.execute(() -> {
			if (player == null || player.world == null)
				return;
			BlockEntity te = player.world.getBlockEntity(pos);
			if (!(te instanceof ITilePacketHandler))
				return;
			((ITilePacketHandler) te).onServerMessageReceived(tag);
		});
	}
}
