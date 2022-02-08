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
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		if (texture != null)
			mc.getTextureManager().bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		if (hovered && hasGradient)
			drawGradientRect(x, y, x + width, y + height, 0x80FFFFFF, 0x80FFFFFF);
		if (texture != null)
			drawModalRectWithCustomSizedTexture(x, y, textureLeft / scale, hovered ? (textureTop + textureTopOff) / scale : textureTop / scale, width, height, 256 / scale, 256 / scale);
		mouseDragged(mc, mouseX, mouseY); // removed in 1.15
		if (!displayString.equals(""))
			mc.fontRenderer.drawString(displayString, x + (width - mc.fontRenderer.getStringWidth(displayString)) / 2, y - 3 + height / 2, 0x404040);
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
		if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height)
			return null;
		return tooltip;
	}
}
