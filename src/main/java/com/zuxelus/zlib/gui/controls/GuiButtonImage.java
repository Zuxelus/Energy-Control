package com.zuxelus.zlib.gui.controls;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiButtonImage extends GuiButton {
	private final ResourceLocation resourceLocation;
	private int xTexStart;
	private int yTexStart;
	private int yDiffText;
	private int color;
	private int hoverColor;
	protected boolean hovered;
	public String tooltip;

	public GuiButtonImage(int id, int left, int top, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation location) {
		super(id, left, top, width, height, "");
		this.xTexStart = xTexStart;
		this.yTexStart = yTexStart;
		this.yDiffText = yDiffText;
		this.resourceLocation = location;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;
		hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(this.resourceLocation);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		int j = yTexStart;
		if (hovered)
			j += yDiffText;

		drawTexturedModalRect(xPosition, yPosition, xTexStart, j, width, height);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		if (displayString != "") {
			FontRenderer fontRenderer = mc.fontRenderer;
			fontRenderer.drawString(displayString, xPosition + (width - fontRenderer.getStringWidth(displayString)) / 2,
					yPosition + (height - 7) / 2, enabled ? hovered ? hoverColor : color : -0x5F5F60);
		}
	}

	public boolean isMouseOver() {
		return hovered;
	}
	
	public void setTextureY(int y) {
		yTexStart = y;
	}

	public GuiButtonImage setText(String text, int color, int hoverColor) {
		displayString = text;
		this.color = color;
		this.hoverColor = hoverColor;
		return this;
	}

	public String getActiveTooltip(int mouseX, int mouseY) {
		if (mouseX < xPosition || mouseX >= xPosition + width || mouseY < yPosition || mouseY >= yPosition + height)
			return null;
		return tooltip;
	}
}
