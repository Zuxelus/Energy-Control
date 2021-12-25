package com.zuxelus.zlib.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.ITilePacketHandler;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PacketTileEntityS2C extends PacketBase {
	public static final Identifier ID = new Identifier(EnergyControl.MODID, "s2c_tile");

	public PacketTileEntityS2C(BlockPos pos, NbtCompound tag) {
		writeBlockPos(pos);
		writeNbt(tag);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void handleClient(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		BlockPos pos = buf.readBlockPos();
		NbtCompound tag = buf.readNbt();
		client.execute(() -> {
			World world = client.player.getWorld();
			if (world != null) {
				BlockEntity te = world.getBlockEntity(pos);
				if (!(te instanceof ITilePacketHandler))
					return;
				((ITilePacketHandler) te).onClientMessageReceived(tag);
			}
		});
	}
}
