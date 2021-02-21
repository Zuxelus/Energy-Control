package com.zuxelus.zlib.network;

import java.util.function.Supplier;

import com.zuxelus.energycontrol.tileentities.ITilePacketHandler;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketTileEntityServer {
	private BlockPos pos;
	private CompoundNBT tag;

	private PacketTileEntityServer(BlockPos pos, CompoundNBT tag) {
		this.pos = pos;
		this.tag = tag;
	}

	public static void handle(PacketTileEntityServer message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerPlayerEntity player = ctx.getSender();
			if (player == null || player.world == null)
				return;
			TileEntity te = player.world.getTileEntity(message.pos);
			if (!(te instanceof ITilePacketHandler))
				return;
			((ITilePacketHandler) te).onServerMessageReceived(message.tag);
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketTileEntityServer pkt, PacketBuffer buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeCompoundTag(pkt.tag);
	}

	public static PacketTileEntityServer decode(PacketBuffer buf) {
		return new PacketTileEntityServer(buf.readBlockPos(), buf.readCompoundTag());
	}
}
