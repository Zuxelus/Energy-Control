package com.zuxelus.energycontrol.renderers;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class RenderHelper {

	/** a (translucent) coloured quad, not affected by light */
	public static void fillRect(PoseStack matrixStack, MultiBufferSource buffer, float left, float top, float right, float bottom, float z, int color) {
		fillRect(matrixStack, buffer, ModRenderTypes.screenColor(), left, top, right, bottom, z, color);
	}

	public static void fillRect(PoseStack matrixStack, MultiBufferSource buffer, RenderType renderType, float left, float top, float right, float bottom, float z, int color) {
		Matrix4f matrix = matrixStack.last().pose();
		VertexConsumer builder = buffer.getBuffer(renderType);
		builder.addVertex(matrix, right, top, z).setColor(color).setLight(LightTexture.FULL_BRIGHT);
		builder.addVertex(matrix, left, top, z).setColor(color).setLight(LightTexture.FULL_BRIGHT);
		builder.addVertex(matrix, left, bottom, z).setColor(color).setLight(LightTexture.FULL_BRIGHT);
		builder.addVertex(matrix, right, bottom, z).setColor(color).setLight(LightTexture.FULL_BRIGHT);
	}

	/** a textured quad drawn full bright; (u0, v0) belongs to (x0, y0) */
	public static void texturedRect(PoseStack matrixStack, MultiBufferSource buffer, ResourceLocation texture,
			float x0, float y0, float x1, float y1, float z, float u0, float v0, float u1, float v1, int color) {
		Matrix4f matrix = matrixStack.last().pose();
		VertexConsumer builder = buffer.getBuffer(ModRenderTypes.screenImage(texture));
		builder.addVertex(matrix, x0, y1, z).setColor(color).setUv(u0, v1).setLight(LightTexture.FULL_BRIGHT);
		builder.addVertex(matrix, x1, y1, z).setColor(color).setUv(u1, v1).setLight(LightTexture.FULL_BRIGHT);
		builder.addVertex(matrix, x1, y0, z).setColor(color).setUv(u1, v0).setLight(LightTexture.FULL_BRIGHT);
		builder.addVertex(matrix, x0, y0, z).setColor(color).setUv(u0, v0).setLight(LightTexture.FULL_BRIGHT);
	}

	public static void drawString(PoseStack matrixStack, MultiBufferSource buffer, String text, float x, float y, int color, Font.DisplayMode mode, int light) {
		if (text == null)
			return;
		Minecraft.getInstance().font.drawInBatch(text, x, y, 0xFF000000 | color, false, matrixStack.last().pose(), buffer, mode, 0, light);
	}

	private RenderHelper() {}
}
