package com.zuxelus.zlib.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonGeneral extends GuiButton {
	private ResourceLocation texture;
	public int textureLeft;
	protected int textureTop;
	public int textureTopOff;
	public int scale;
	public String tooltip;
	private boolean hasGradient;

	public GuiButtonGeneral(int id, int left, int top, int width, int height, ResourceLocation texture, int textureLeft, int textureTop) {
		this(id, left, top, width, height, "", texture, textureLeft, textureTop, 0, "");
	}

	public GuiButtonGeneral(int id, int left, int top, int width, int height, ResourceLocation texture, int textureLeft, int textureTop, int textureTopOff) {
		this(id, left, top, width, height, "", texture, textureLeft, textureTop, textureTopOff, "");
	}

	public GuiButtonGeneral(int id, int left, int top, int width, int height, String text) {
		this(id, left, top, width, height, text, null, 0, 0, 0, "");
	}

	public GuiButtonGeneral(int id, int left, int top, int width, int height, String text, ResourceLocation texture, int textureLeft, int textureTop, int textureTopOff, String tooltip) {
		super(id, left, top, width, height, text);
		this.texture = texture;
		this.textureLeft = textureLeft;
		this.textureTop = textureTop;
		this.textureTopOff = textureTopOff;
		this.tooltip = tooltip;
		scale = 1;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (!visible)
			return;

		if (texture != null)
			mc.getTextureManager().bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		if (hovered && hasGradient)
			drawGradientRect(xPosition, yPosition, xPosition + width, yPosition + height, 0x80FFFFFF, 0x80FFFFFF);
		if (texture != null)
			drawModalRectWithCustomSizedTexture(xPosition, yPosition, textureLeft / scale, hovered ? (textureTop + textureTopOff) / scale : textureTop / scale, width, height, 256 / scale, 256 / scale);
		mouseDragged(mc, mouseX, mouseY);
		if (!displayString.equals(""))
			mc.fontRendererObj.drawString(displayString, xPosition + (width - mc.fontRendererObj.getStringWidth(displayString)) / 2, yPosition - 3 + height / 2, 0x404040);
	}

	public GuiButtonGeneral setGradient() {
		hasGradient = true;
		return this;
	}

	public GuiButtonGeneral setScale(int scale) {
		this.scale = scale;
		return this;
	}

	public void setTextureTop(int y) {
		textureTop = y;
	}

	public String getActiveTooltip(int mouseX, int mouseY) {
		if (mouseX < xPosition || mouseX >= xPosition + width || mouseY < yPosition || mouseY >= yPosition + height)
			return null;
		return tooltip;
	}
}
