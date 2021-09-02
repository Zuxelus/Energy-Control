package com.zuxelus.energycontrol.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;

/**
 * Used to draw (progress) bars on Info Panels
 */
public interface IHasBars {
	
	boolean enableBars(ItemStack stack);

	void renderBars(TextureManager manager, float displayWidth, float displayHeight, ICardReader reader, MatrixStack matrixStack);

	// copy from AbstractGui.fillGradient()
	static void drawTransparentRect(MatrixStack matrixStack, float left, float top, float right, float bottom, float zLevel, int color) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.disableAlphaTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		drawPositionColor(matrixStack.getLast().getMatrix(), bufferbuilder, left, top, right, bottom, zLevel, color);
		tessellator.draw();
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.disableDepthTest();
		RenderSystem.enableTexture();
	}

	static void drawPositionColor(Matrix4f matrix, BufferBuilder builder, float left, float top, float right, float bottom, float zLevel, int color) {
		float f = (color >> 24 & 255) / 255.0F;
		float f1 = (color >> 16 & 255) / 255.0F;
		float f2 = (color >> 8 & 255) / 255.0F;
		float f3 = (color & 255) / 255.0F;
		builder.pos(matrix, right, top, zLevel).color(f1, f2, f3, f).endVertex();
		builder.pos(matrix, left, top, zLevel).color(f1, f2, f3, f).endVertex();
		builder.pos(matrix, left, bottom, zLevel).color(f1, f2, f3, f).endVertex();
		builder.pos(matrix, right, bottom, zLevel).color(f1, f2, f3, f).endVertex();
	}
}
