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
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import net.minecraft.util.math.Vec3d;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Inject(method = "render", at = @At("TAIL"))
	private void inject(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
		if (EnergyControlClient.holo_panels.isEmpty())
			return;
		MinecraftClient client = MinecraftClient.getInstance();
		BlockEntityRenderDispatcher dispatcher = client.getBlockEntityRenderDispatcher();

		Vec3d vector3d = camera.getPos();
		double d0 = vector3d.getX();
		double d1 = vector3d.getY();
		double d2 = vector3d.getZ();

		// since 1.20.5 render() gets the view rotation as a matrix instead of a MatrixStack, and the model-view stack is already popped here
		MatrixStack matrices = new MatrixStack();
		matrices.multiplyPositionMatrix(matrix4f);
		Immediate buffers = client.getBufferBuilders().getEntityVertexConsumers();
		for (BlockEntity te : EnergyControlClient.holo_panels) {
			matrices.push();
			BlockPos pos = te.getPos();
			matrices.translate(pos.getX() - d0, pos.getY() - d1, pos.getZ() - d2);
			// -1 tells TileEntityHoloPanelRenderer this is the deferred pass
			dispatcher.render(te, -1, matrices, buffers);
			matrices.pop();
		}
		buffers.draw();
		EnergyControlClient.holo_panels.clear();
	}
}
