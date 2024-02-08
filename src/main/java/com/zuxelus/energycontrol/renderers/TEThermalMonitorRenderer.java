package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class TEThermalMonitorRenderer implements BlockEntityRenderer<TileEntityThermalMonitor> {
	private static final ResourceLocation TEXTURE0 = new ResourceLocation(EnergyControl.MODID, "textures/block/thermal_monitor/all0.png");
	private static final ResourceLocation TEXTURE1 = new ResourceLocation(EnergyControl.MODID, "textures/block/thermal_monitor/all1.png");
	private static final ResourceLocation TEXTURE2 = new ResourceLocation(EnergyControl.MODID, "textures/block/thermal_monitor/all2.png");
	private final Font font;

	public TEThermalMonitorRenderer(Context ctx) {
		font = ctx.getFont();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void render(TileEntityThermalMonitor te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();

		CubeSmallRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		VertexConsumer vertexBuilder;
		switch (te.getStatus()) {
		case 0:
			vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTURE1));
			break;
		case 1:
			vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTURE2));
			break;
		default:
			vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTURE0));
			break;
		}
		CubeSmallRenderer.MODEL.render(matrixStack, vertexBuilder, CubeSmallRenderer.getBlockLight(te), combinedOverlay);

		matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		matrixStack.translate(-0.5F, -0.55F, -0.4376F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);

		int value = te.getHeatLevel();
		font.drawInBatch(String.valueOf(value), -font.width(String.valueOf(value)) / 2, -font.lineHeight, 0x000000, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
		matrixStack.popPose();
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
