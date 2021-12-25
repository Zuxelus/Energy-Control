package com.zuxelus.energycontrol.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.zuxelus.energycontrol.EnergyControlClient;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Inject(method = "render", at = @At("TAIL"))
	private void inject(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		BlockEntityRenderDispatcher dispatcher = client.getBlockEntityRenderDispatcher();
		Camera info = client.gameRenderer.getCamera();
		info.update(client.world, client.getCameraEntity() == null ? client.player : client.getCameraEntity(),
				!client.options.getPerspective().isFirstPerson(), client.options.getPerspective().isFrontView(),
				tickDelta);

		Vec3d vector3d = info.getPos();
		double d0 = vector3d.getX();
		double d1 = vector3d.getY();
		double d2 = vector3d.getZ();

		Immediate buffers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		for (BlockEntity te : EnergyControlClient.holo_panels) {
			matrices.push();
			BlockPos pos = te.getPos();
			matrices.translate(pos.getX() - d0, pos.getY() - d1, pos.getZ() - d2);
			dispatcher.render(te, -1, matrices, buffers);
			matrices.pop();
		}
		EnergyControlClient.holo_panels.clear();
	}
}
