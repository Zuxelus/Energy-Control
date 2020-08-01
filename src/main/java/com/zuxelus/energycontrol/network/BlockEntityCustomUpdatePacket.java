package com.zuxelus.energycontrol.network;

import java.lang.reflect.Field;

import com.zuxelus.energycontrol.blockentities.ITilePacketHandler;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class BlockEntityCustomUpdatePacket extends BlockEntityUpdateS2CPacket {

	public BlockEntityCustomUpdatePacket() { }

	public BlockEntityCustomUpdatePacket(BlockPos pos, int blockEntityType, CompoundTag tag) {
		super(pos, blockEntityType, tag);
	}

	@Override
	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		if (!(clientPlayPacketListener instanceof ClientPlayNetworkHandler))
			return;
		
		ClientPlayNetworkHandler handler = (ClientPlayNetworkHandler) clientPlayPacketListener;
		try {
			Field field = ClientPlayNetworkHandler.class.getDeclaredField("client");
			field.setAccessible(true);
			MinecraftClient client = (MinecraftClient) field.get(handler);

			NetworkThreadUtils.forceMainThread(this, handler, client);
			if (client.world.isChunkLoaded(getPos())) {
				BlockEntity be = client.world.getBlockEntity(getPos());
				if (be instanceof ITilePacketHandler)
					((ITilePacketHandler) be).onDataPacket(this);
			}
		} catch (Throwable t) { }
	}
}
