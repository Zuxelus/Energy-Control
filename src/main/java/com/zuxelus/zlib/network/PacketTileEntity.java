package com.zuxelus.zlib.network;

import java.util.function.Supplier;

import com.zuxelus.zlib.tileentities.ITilePacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketTileEntity {
	private BlockPos pos;
	private CompoundNBT tag;

	public PacketTileEntity(BlockPos pos, CompoundNBT tag) {
		this.pos = pos;
		this.tag = tag;
	}

	public static void handle(PacketTileEntity message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide() == LogicalSide.SERVER) {
				ServerPlayerEntity player = ctx.getSender();
				if (player == null || player.world == null)
					return;
				TileEntity te = player.world.getTileEntity(message.pos);
				if (!(te instanceof ITilePacketHandler))
					return;
				((ITilePacketHandler) te).onServerMessageReceived(message.tag);
			} else {
				@SuppressWarnings("resource")
				ClientWorld world = Minecraft.getInstance().world;
				if (world != null) {
					TileEntity te = world.getTileEntity(message.pos);
					if (!(te instanceof ITilePacketHandler))
						return;
					((ITilePacketHandler) te).onClientMessageReceived(message.tag);
				}
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketTileEntity pkt, PacketBuffer buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeCompoundTag(pkt.tag);
	}

	public static PacketTileEntity decode(PacketBuffer buf) {
		return new PacketTileEntity(buf.readBlockPos(), buf.readCompoundTag());
	}
}
