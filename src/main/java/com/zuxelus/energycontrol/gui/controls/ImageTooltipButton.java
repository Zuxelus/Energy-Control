package com.zuxelus.energycontrol.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ImageTooltipButton extends GuiButton {
	private final ResourceLocation location;
	private final int xTexStart;
	private int yTexStart;
	private int yDiffText;
	public String tooltip;

	public ImageTooltipButton(int id, int left, int top, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation location) {
		super(id, left, top, width, height, "");
		this.xTexStart = xTexStart;
		this.yTexStart = yTexStart;
		this.yDiffText = yDiffText;
		this.location = location;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		mc.getTextureManager().bindTexture(location);
		GlStateManager.disableDepth();
		drawTexturedModalRect(x, y, xTexStart, hovered ? yTexStart + yDiffText : yTexStart, width, height);
		GlStateManager.enableDepth();
	}

	public void setTextureY(int y) {
		yTexStart = y;
	}

	public String getActiveTooltip(int mouseX, int mouseY) {
		if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height)
			return null;
		return tooltip;
	}
}
