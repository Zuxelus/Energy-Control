package com.zuxelus.energycontrol.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.zuxelus.energycontrol.tileentities.ITilePacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {

	@Inject(method = "handleBlockEntityData(Lnet/minecraft/network/protocol/game/ClientboundBlockEntityDataPacket;)V", at = @At("RETURN"))
	private void onBlockEntityUpdate(ClientboundBlockEntityDataPacket packet, CallbackInfo ci) {
		Level world = Minecraft.getInstance().level;
		if (world == null)
			return;
		BlockPos pos = packet.getPos();
		if (world.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))) {
			BlockEntity be = world.getBlockEntity(packet.getPos());
			if (be instanceof ITilePacketHandler)
				((ITilePacketHandler) be).onDataPacket(packet);
		}
	}
}
