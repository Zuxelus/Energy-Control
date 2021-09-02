package com.zuxelus.zlib.network;

import java.util.function.Supplier;

import com.zuxelus.energycontrol.tileentities.ITilePacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketTileEntityClient {
	private BlockPos pos;
	private CompoundNBT tag;

	private PacketTileEntityClient(BlockPos pos, CompoundNBT tag) {
		this.pos = pos;
		this.tag = tag;
	}

	public static void handle(PacketTileEntityClient message, Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			@SuppressWarnings("resource")
			ClientWorld world = Minecraft.getInstance().level;
			if (world != null) {
				TileEntity te = world.getBlockEntity(message.pos);
				if (!(te instanceof ITilePacketHandler))
					return;
				((ITilePacketHandler) te).onClientMessageReceived(message.tag);
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void encode(PacketTileEntityClient pkt, PacketBuffer buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeNbt(pkt.tag);
	}

	public static PacketTileEntityClient decode(PacketBuffer buf) {
		return new PacketTileEntityClient(buf.readBlockPos(), buf.readNbt());
	}
}
