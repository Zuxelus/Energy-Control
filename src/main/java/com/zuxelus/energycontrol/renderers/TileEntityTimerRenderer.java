package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class TileEntityTimerRenderer implements BlockEntityRenderer<TileEntityTimer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/block/timer/all.png");
	private static final ResourceLocation TEXTURE_ACTIVE = new ResourceLocation(EnergyControl.MODID + ":textures/block/timer/active.png");
	private final Font font;

	public TileEntityTimerRenderer(Context ctx) {
		font = ctx.getFont();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void render(TileEntityTimer te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();

		CubeSmallRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		VertexConsumer vertexBuilder = te.getIsWorking() ? buffer.getBuffer(RenderType.entitySolid(TEXTURE_ACTIVE)) : buffer.getBuffer(RenderType.entitySolid(TEXTURE));
		CubeRenderer.MODEL.render(matrixStack, vertexBuilder, TileEntityInfoPanelRenderer.getBlockLight(te), combinedOverlay);
		String time = te.getTimeString();
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
		matrixStack.translate(0.5F, 0.575F, -0.4376F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);
		font.drawInBatch(time, -font.width(time) / 2, -font.lineHeight, 0x000000, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
		matrixStack.popPose();
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
