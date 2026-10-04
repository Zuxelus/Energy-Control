package com.zuxelus.zlib.network;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.ITilePacketHandler;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record PacketTileEntityS2C(BlockPos pos, NbtCompound tag) implements PacketBase {
	public static final Id<PacketTileEntityS2C> ID = new Id<>(Identifier.of(EnergyControl.MODID, "s2c_tile"));
	public static final PacketCodec<RegistryByteBuf, PacketTileEntityS2C> CODEC = PacketCodec.tuple(
			BlockPos.PACKET_CODEC, PacketTileEntityS2C::pos,
			PacketCodecs.NBT_COMPOUND, PacketTileEntityS2C::tag,
			PacketTileEntityS2C::new);

	@Override
	public Id<PacketTileEntityS2C> getId() {
		return ID;
	}

	public static void handleClient(PacketTileEntityS2C packet, ClientPlayNetworking.Context context) {
		MinecraftClient client = context.client();
		client.execute(() -> {
			World world = client.player.getWorld();
			if (world != null) {
				BlockEntity te = world.getBlockEntity(packet.pos());
				if (!(te instanceof ITilePacketHandler))
					return;
				((ITilePacketHandler) te).onClientMessageReceived(packet.tag());
			}
		});
	}
}
