package com.zuxelus.energycontrol.api;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Matrix4f;

/**
 * Used to draw (progress) bars on Info Panels
 */
public interface IHasBars {
	
	boolean enableBars(ItemStack stack);

	void renderBars(float displayWidth, float displayHeight, ICardReader reader, MatrixStack matrixStack);

	// copy from GuiComponent.fillGradient()
	static void drawTransparentRect(MatrixStack matrixStack, float left, float top, float right, float bottom, float zLevel, int color) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tessellator tesselator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuffer();
		bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		drawPositionColor(matrixStack.peek().getPositionMatrix(), bufferbuilder, left, top, right, bottom, zLevel, color);
		tesselator.draw();
		RenderSystem.disableBlend();
		RenderSystem.disableDepthTest();
		RenderSystem.enableTexture();
	}

	static void drawPositionColor(Matrix4f matrix, BufferBuilder builder, float left, float top, float right, float bottom, float zLevel, int color) {
		float f = (color >> 24 & 255) / 255.0F;
		float f1 = (color >> 16 & 255) / 255.0F;
		float f2 = (color >> 8 & 255) / 255.0F;
		float f3 = (color & 255) / 255.0F;
		builder.vertex(matrix, right, top, zLevel).color(f1, f2, f3, f).next();
		builder.vertex(matrix, left, top, zLevel).color(f1, f2, f3, f).next();
		builder.vertex(matrix, left, bottom, zLevel).color(f1, f2, f3, f).next();
		builder.vertex(matrix, right, bottom, zLevel).color(f1, f2, f3, f).next();
	}
}
