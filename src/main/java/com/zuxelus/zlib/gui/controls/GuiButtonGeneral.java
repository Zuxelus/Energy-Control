package com.zuxelus.zlib.gui.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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

	public GuiButtonGeneral(int left, int top, int width, int height, ResourceLocation texture, int textureLeft, int textureTop, Button.IPressable onPress) {
		this(left, top, width, height, StringTextComponent.EMPTY, texture, textureLeft, textureTop, 0, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, ResourceLocation texture, int textureLeft, int textureTop, int textureTopOff, Button.IPressable onPress) {
		this(left, top, width, height, StringTextComponent.EMPTY, texture, textureLeft, textureTop, textureTopOff, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, ITextComponent text, Button.IPressable onPress) {
		this(left, top, width, height, text, null, 0, 0, 0, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, ITextComponent text, ResourceLocation texture, int textureLeft, int textureTop, int textureTopOff, String tooltip, Button.IPressable onPress) {
		super(left, top, width, height, text, onPress);
		this.texture = texture;
		this.textureLeft = textureLeft;
		this.textureTop = textureTop;
		this.textureTopOff = textureTopOff;
		this.tooltip = tooltip;
		scale = 1;
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontRenderer = minecraft.fontRenderer;
		if (texture != null)
			minecraft.getTextureManager().bindTexture(texture);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		//isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		/*RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);*/
		if (isHovered && hasGradient)
			fillGradient(matrixStack, x, y, x + width, y + height, 0x80FFFFFF, 0x80FFFFFF);
		if (texture != null)
			blit(matrixStack, x, y, textureLeft / scale, isHovered ? (textureTop + textureTopOff) / scale : textureTop / scale, width, height, 256 / scale, 256 / scale);
		//mouseDragged(mc, mouseX, mouseY);
		String displayString = getMessage().getString();
		if (!displayString.equals(""))
			fontRenderer.drawString(matrixStack, displayString, x + (width - fontRenderer.getStringWidth(displayString)) / 2, y - 3 + height / 2, 0x404040);
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
