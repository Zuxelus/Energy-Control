package com.zuxelus.energycontrol.renderers;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class TERemoteThermalMonitorRenderer implements BlockEntityRenderer<TileEntityRemoteThermalMonitor> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "textures/block/remote_thermal_monitor/all.png");
	private final Font font;

	public TERemoteThermalMonitorRenderer(Context ctx) {
		font = ctx.getFont();
	}

	@Override
	public void render(TileEntityRemoteThermalMonitor te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();

		CubeRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTURE));
		CubeRenderer.MODEL.render(matrixStack, vertexBuilder, CubeRenderer.getBlockLight(te), combinedOverlay);
		matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		matrixStack.translate(0.0F, -0.5F, 0.001F);

		int status = te.getStatus();
		int heat = te.getHeat();
		int level = te.getHeatLevel();
		if (status > -2) {
			float rate = 1;
			if (status > -1)
				rate = Math.round((1 - Math.min((float) heat / level, 1)) * 16) / (float) 16;
			
			VertexConsumer bar = buffer.getBuffer(ModRenderTypes.screenImage(TEXTURE));
			Matrix4f matrix = matrixStack.last().pose();
			bar.addVertex(matrix, rate, 0, 0).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(rate * 0.25F, 0).setLight(LightTexture.FULL_BRIGHT);
			bar.addVertex(matrix, 1, 0, 0).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.25F, 0).setLight(LightTexture.FULL_BRIGHT);
			bar.addVertex(matrix, 1.0F, 0.75F, 0).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(0.25F, 0.1875F).setLight(LightTexture.FULL_BRIGHT);
			bar.addVertex(matrix, rate, 0.75F, 0).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(rate * 0.25F, 0.1875F).setLight(LightTexture.FULL_BRIGHT);
		}

		matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
		matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		matrixStack.translate(-0.5F, -0.125F, 0.0F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);

		String text = Integer.toString(level);
		font.drawInBatch(text, -font.width(text) / 2, -font.lineHeight, 0x000000, false, matrixStack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, LightTexture.FULL_BRIGHT);
		matrixStack.popPose();
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
