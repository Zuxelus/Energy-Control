package com.zuxelus.zlib.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiButtonGeneral extends Button {
	private ResourceLocation texture;
	public int textureLeft;
	protected int textureTop;
	public int textureTopOff;
	public int scale;
	public String tooltip;
	private boolean hasGradient;

	public GuiButtonGeneral(int left, int top, int width, int height, ResourceLocation texture, int textureLeft, int textureTop, Button.OnPress onPress) {
		this(left, top, width, height, CommonComponents.EMPTY, texture, textureLeft, textureTop, 0, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, ResourceLocation texture, int textureLeft, int textureTop, int textureTopOff, Button.OnPress onPress) {
		this(left, top, width, height, CommonComponents.EMPTY, texture, textureLeft, textureTop, textureTopOff, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, Component text, Button.OnPress onPress) {
		this(left, top, width, height, text, null, 0, 0, 0, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, Component text, ResourceLocation texture, int textureLeft, int textureTop, int textureTopOff, String tooltip, Button.OnPress onPress) {
		super(left, top, width, height, text, onPress, null);
		this.texture = texture;
		this.textureLeft = textureLeft;
		this.textureTop = textureTop;
		this.textureTopOff = textureTopOff;
		this.tooltip = tooltip;
		scale = 1;
	}

	@Override
	public void renderWidget(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;
		//RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		if (isHovered && hasGradient)
			matrixStack.fillGradient(getX(), getY(), getX() + width, getY() + height, 0x80FFFFFF, 0x80FFFFFF);
		if (texture != null)
			matrixStack.blit(texture, getX(), getY(), textureLeft / scale, isHovered ? (textureTop + textureTopOff) / scale : textureTop / scale, width, height, 256 / scale, 256 / scale);
		String displayString = getMessage().getString();
		if (!displayString.equals(""))
			matrixStack.drawString(fontRenderer, displayString, getX() + (width - fontRenderer.width(displayString)) / 2, getY() - 3 + height / 2, 0x404040, false);
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
