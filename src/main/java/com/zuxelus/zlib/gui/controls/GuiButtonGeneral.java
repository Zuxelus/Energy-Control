package com.zuxelus.zlib.gui.controls;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class GuiButtonGeneral extends Button {
	private Identifier texture;
	public int textureLeft;
	protected int textureTop;
	public int textureTopOff;
	public int scale;
	public String tooltip;
	private boolean hasGradient;

	public GuiButtonGeneral(int left, int top, int width, int height, Identifier texture, int textureLeft, int textureTop, Button.OnPress onPress) {
		this(left, top, width, height, CommonComponents.EMPTY, texture, textureLeft, textureTop, 0, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, Identifier texture, int textureLeft, int textureTop, int textureTopOff, Button.OnPress onPress) {
		this(left, top, width, height, CommonComponents.EMPTY, texture, textureLeft, textureTop, textureTopOff, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, Component text, Button.OnPress onPress) {
		this(left, top, width, height, text, null, 0, 0, 0, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, Component text, Identifier texture, int textureLeft, int textureTop, int textureTopOff, String tooltip, Button.OnPress onPress) {
		super(left, top, width, height, text, onPress, DEFAULT_NARRATION);
		this.texture = texture;
		this.textureLeft = textureLeft;
		this.textureTop = textureTop;
		this.textureTopOff = textureTopOff;
		this.tooltip = tooltip;
		scale = 1;
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;
		if (isHovered && hasGradient)
			context.fillGradient(getX(), getY(), getX() + width, getY() + height, 0x80FFFFFF, 0x80FFFFFF);
		if (texture != null)
			context.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), textureLeft / scale, isHovered ? (textureTop + textureTopOff) / scale : textureTop / scale, width, height, 256 / scale, 256 / scale);
		String displayString = getMessage().getString();
		if (!displayString.equals(""))
			context.text(fontRenderer, displayString, getX() + (width - fontRenderer.width(displayString)) / 2, getY() - 3 + height / 2, 0xFF404040, false);
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
