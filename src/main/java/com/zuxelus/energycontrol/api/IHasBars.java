package com.zuxelus.energycontrol.api;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

/**
 * Used to draw (progress) bars on Info Panels
 */
public interface IHasBars {

	boolean enableBars(ItemStack stack);

	void renderBars(TextureManager manager, double displayWidth, double displayHeight, ICardReader reader);

	// copy from Gui.drawGradientRect()
	static void drawTransparentRect(double left, double top, double right, double bottom, double zLevel, int color) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		drawPositionColor(tessellator, left, top, right, bottom, zLevel, color);
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	static void drawPositionColor(Tessellator builder, double left, double top, double right, double bottom, double zLevel, int color) {
		float f = (color >> 24 & 255) / 255.0F;
		float f1 = (color >> 16 & 255) / 255.0F;
		float f2 = (color >> 8 & 255) / 255.0F;
		float f3 = (color & 255) / 255.0F;
		builder.setColorRGBA_F(f1, f2, f3, f);
		builder.addVertex(right, top, zLevel);
		builder.addVertex(left, top, zLevel);
		builder.addVertex(left, bottom, zLevel);
		builder.addVertex(right, bottom, zLevel);
	}
}
