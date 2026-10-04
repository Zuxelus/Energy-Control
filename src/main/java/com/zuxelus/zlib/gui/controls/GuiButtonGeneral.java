package com.zuxelus.zlib.gui.controls;

import net.minecraft.screen.ScreenTexts;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiButtonGeneral extends ButtonWidget {
	private Identifier texture;
	public int textureLeft;
	protected int textureTop;
	public int textureTopOff;
	public int scale;
	public String tooltip;
	private boolean hasGradient;

	public GuiButtonGeneral(int left, int top, int width, int height, Identifier texture, int textureLeft, int textureTop, ButtonWidget.PressAction onPress) {
		this(left, top, width, height, ScreenTexts.EMPTY, texture, textureLeft, textureTop, 0, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, Identifier texture, int textureLeft, int textureTop, int textureTopOff, ButtonWidget.PressAction onPress) {
		this(left, top, width, height, ScreenTexts.EMPTY, texture, textureLeft, textureTop, textureTopOff, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, Text text, ButtonWidget.PressAction onPress) {
		this(left, top, width, height, text, null, 0, 0, 0, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, Text text, Identifier texture, int textureLeft, int textureTop, int textureTopOff, String tooltip, ButtonWidget.PressAction onPress) {
		super(left, top, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
		this.texture = texture;
		this.textureLeft = textureLeft;
		this.textureTop = textureTop;
		this.textureTopOff = textureTopOff;
		this.tooltip = tooltip;
		scale = 1;
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		MinecraftClient minecraft = MinecraftClient.getInstance();
		TextRenderer fontRenderer = minecraft.textRenderer;
		if (hovered && hasGradient)
			context.fillGradient(getX(), getY(), getX() + width, getY() + height, 0x80FFFFFF, 0x80FFFFFF);
		if (texture != null)
			context.drawTexture(texture, getX(), getY(), textureLeft / scale, hovered ? (textureTop + textureTopOff) / scale : textureTop / scale, width, height, 256 / scale, 256 / scale);
		String displayString = getMessage().getString();
		if (!displayString.equals(""))
			context.drawText(fontRenderer, displayString, getX() + (width - fontRenderer.getWidth(displayString)) / 2, getY() - 3 + height / 2, 0x404040, false);
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
		if (mouseX < getX() || mouseX >= getX() + width || mouseY < getY() || mouseY >= getY() + height)
			return null;
		return tooltip;
	}
}
