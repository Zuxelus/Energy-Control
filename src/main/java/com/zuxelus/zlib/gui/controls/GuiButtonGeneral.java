package com.zuxelus.zlib.gui.controls;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiButtonGeneral extends GuiButton {
	private ResourceLocation texture;
	public int textureLeft;
	protected int textureTop;
	public int textureTopOff;
	public int scale;
	private int color = 0x404040;
	private int hoverColor = 0x404040;
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
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		if (field_146123_n && hasGradient)
			drawGradientRect(xPosition, yPosition, xPosition + width, yPosition + height, 0x80FFFFFF, 0x80FFFFFF);
		if (texture != null)
			func_146110_a(xPosition, yPosition, textureLeft / scale, field_146123_n ? (textureTop + textureTopOff) / scale : textureTop / scale, width, height, 256 / scale, 256 / scale);
		mouseDragged(mc, mouseX, mouseY);
		if (!displayString.equals("")) {
			FontRenderer fontRenderer = mc.fontRenderer;
			fontRenderer.drawString(displayString, xPosition + (width - fontRenderer.getStringWidth(displayString)) / 2,
					yPosition + (height - 7) / 2, enabled ? field_146123_n ? hoverColor : color : -0x5F5F60);
		}
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

	public GuiButtonGeneral setText(String text, int color, int hoverColor) {
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
