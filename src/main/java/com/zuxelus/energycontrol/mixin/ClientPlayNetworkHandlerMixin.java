package com.zuxelus.energycontrol.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.zuxelus.energycontrol.tileentities.ITilePacketHandler;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Inject(method = "onBlockEntityUpdate(Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;)V", at = @At("RETURN"))
	private void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
		World world = MinecraftClient.getInstance().world;
		if (world == null)
			return;
		BlockPos pos = packet.getPos();
		if (world.getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
			BlockEntity be = world.getBlockEntity(packet.getPos());
			if (be instanceof ITilePacketHandler)
				((ITilePacketHandler) be).onDataPacket(packet);
		}
	}
}
