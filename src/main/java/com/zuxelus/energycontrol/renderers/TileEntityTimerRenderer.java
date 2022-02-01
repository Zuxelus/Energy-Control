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
	private static final CubeRenderer model = new CubeRenderer(2, 0, 2, 28, 14, 28, 128, 64, 0, 0);
	private final Font font;

	public TileEntityTimerRenderer(Context ctx) {
		font = ctx.getFont();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void render(TileEntityTimer te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		switch (te.getFacing()) {
		case UP:
			switch (te.getRotation()) {
			case NORTH:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				break;
			case WEST:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case EAST:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case NORTH:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			matrixStack.translate(-1.0F, -1.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case EAST:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case WEST:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case SOUTH:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
			matrixStack.translate(0.0F, 0.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case WEST:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case EAST:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case DOWN:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case NORTH:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case EAST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
			matrixStack.translate(-1.0F, 0.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case SOUTH:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case NORTH:
				matrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		}

		VertexConsumer vertexBuilder = te.getIsWorking() ? buffer.getBuffer(RenderType.entitySolid(TEXTURE_ACTIVE)) : buffer.getBuffer(RenderType.entitySolid(TEXTURE));
		model.render(matrixStack, vertexBuilder, TileEntityInfoPanelRenderer.getBlockLight(te), combinedOverlay);
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
