package com.zuxelus.zlib.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.ITilePacketHandler;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record PacketTileEntityS2C(BlockPos pos, CompoundTag tag) implements CustomPacketPayload {
	public static final Type<PacketTileEntityS2C> ID = new Type<>(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "s2c_tile"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketTileEntityS2C> CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, PacketTileEntityS2C::pos,
			ByteBufCodecs.COMPOUND_TAG, PacketTileEntityS2C::tag,
			PacketTileEntityS2C::new);

	@Override
	public Type<PacketTileEntityS2C> type() {
		return ID;
	}

	public static void handleClient(PacketTileEntityS2C packet, ClientPlayNetworking.Context context) {
		Minecraft client = context.client();
		client.execute(() -> {
			Level world = client.player.level();
			if (world != null) {
				BlockEntity te = world.getBlockEntity(packet.pos());
				if (!(te instanceof ITilePacketHandler))
					return;
				((ITilePacketHandler) te).onClientMessageReceived(packet.tag());
			}
		});
	}
}
