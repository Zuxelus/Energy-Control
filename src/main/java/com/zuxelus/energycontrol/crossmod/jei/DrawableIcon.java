package com.zuxelus.energycontrol.crossmod.jei;

import mezz.jei.api.gui.IDrawableStatic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class DrawableIcon implements IDrawableStatic { // 1.10.2
	private final ResourceLocation resourceLocation;
	private final int u;
	private final int v;
	private final int width;
	private final int height;
	private final int textureWidth;
	private final int textureHeight;

	public DrawableIcon(ResourceLocation resourceLocation, int u, int v, int width, int height, int textureWidth, int textureHeight) {
		this.resourceLocation = resourceLocation;
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void draw(Minecraft minecraft) {
		draw(minecraft, 0, 0);
	}

	public void draw(Minecraft minecraft, int xOffset, int yOffset) {
		draw(minecraft, xOffset, yOffset, 0, 0, 0, 0);
	}

	public void draw(Minecraft minecraft, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight) {
		minecraft.getTextureManager().bindTexture(this.resourceLocation);
		int x = xOffset + maskLeft;
		int y = yOffset + maskTop;
		int u = this.u + maskLeft;
		int v = this.v + maskTop;
		int width = this.width - maskRight - maskLeft;
		int height = this.height - maskBottom - maskTop;
		Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, textureWidth, textureHeight);
	}
}
