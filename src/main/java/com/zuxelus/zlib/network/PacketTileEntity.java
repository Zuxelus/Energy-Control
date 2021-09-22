package com.zuxelus.zlib.network;

import java.util.function.Supplier;

import com.zuxelus.energycontrol.tileentities.ITilePacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;

public class PacketTileEntity {
	private BlockPos pos;
	private CompoundTag tag;

	public PacketTileEntity(BlockPos pos, CompoundTag tag) {
		this.pos = pos;
		this.tag = tag;
	}

	public static void handle(PacketTileEntity message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide() == LogicalSide.SERVER) {
				ServerPlayer player = ctx.getSender();
				if (player == null || player.level == null)
					return;
				BlockEntity te = player.level.getBlockEntity(message.pos);
				if (!(te instanceof ITilePacketHandler))
					return;
				((ITilePacketHandler) te).onServerMessageReceived(message.tag);
			} else {
				@SuppressWarnings("resource")
				ClientLevel world = Minecraft.getInstance().level;
				if (world != null) {
					BlockEntity te = world.getBlockEntity(message.pos);
					if (!(te instanceof ITilePacketHandler))
						return;
					((ITilePacketHandler) te).onClientMessageReceived(message.tag);
				}
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketTileEntity pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeNbt(pkt.tag);
	}

	public static PacketTileEntity decode(FriendlyByteBuf buf) {
		return new PacketTileEntity(buf.readBlockPos(), buf.readNbt());
	}
}
