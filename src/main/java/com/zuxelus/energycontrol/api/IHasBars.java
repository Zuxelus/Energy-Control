package com.zuxelus.energycontrol.api;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

/**
 * Used to draw (progress) bars on Info Panels
 */
public interface IHasBars {
	
	boolean enableBars(ItemStack stack);

	void renderBars(TextureManager manager, double displayWidth, double displayHeight, ICardReader reader);

	// copy from Gui.drawGradientRect()
	static void drawTransparentRect(double left, double top, double right, double bottom, double zLevel, int color) {
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		drawPositionColor(bufferbuilder, left, top, right, bottom, zLevel, color);
		tessellator.draw();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	static void drawPositionColor(BufferBuilder builder, double left, double top, double right, double bottom, double zLevel, int color) {
		float f = (color >> 24 & 255) / 255.0F;
		float f1 = (color >> 16 & 255) / 255.0F;
		float f2 = (color >> 8 & 255) / 255.0F;
		float f3 = (color & 255) / 255.0F;
		builder.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex();
		builder.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
		builder.pos(left, bottom, zLevel).color(f1, f2, f3, f).endVertex();
		builder.pos(right, bottom, zLevel).color(f1, f2, f3, f).endVertex();
	}
}
