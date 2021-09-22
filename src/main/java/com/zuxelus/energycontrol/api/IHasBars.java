package com.zuxelus.energycontrol.api;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;

/**
 * Used to draw (progress) bars on Info Panels
 */
public interface IHasBars {
	
	boolean enableBars(ItemStack stack);

	void renderBars(float displayWidth, float displayHeight, ICardReader reader, PoseStack matrixStack);

	// copy from GuiComponent.fillGradient()
	static void drawTransparentRect(PoseStack matrixStack, float left, float top, float right, float bottom, float zLevel, int color) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		drawPositionColor(matrixStack.last().pose(), bufferbuilder, left, top, right, bottom, zLevel, color);
		tesselator.end();
		RenderSystem.disableBlend();
		RenderSystem.disableDepthTest();
		RenderSystem.enableTexture();
	}

	static void drawPositionColor(Matrix4f matrix, BufferBuilder builder, float left, float top, float right, float bottom, float zLevel, int color) {
		float f = (color >> 24 & 255) / 255.0F;
		float f1 = (color >> 16 & 255) / 255.0F;
		float f2 = (color >> 8 & 255) / 255.0F;
		float f3 = (color & 255) / 255.0F;
		builder.vertex(matrix, right, top, zLevel).color(f1, f2, f3, f).endVertex();
		builder.vertex(matrix, left, top, zLevel).color(f1, f2, f3, f).endVertex();
		builder.vertex(matrix, left, bottom, zLevel).color(f1, f2, f3, f).endVertex();
		builder.vertex(matrix, right, bottom, zLevel).color(f1, f2, f3, f).endVertex();
	}
}
