package com.zuxelus.zlib.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
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
		this(left, top, width, height, LiteralText.EMPTY, texture, textureLeft, textureTop, 0, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, Identifier texture, int textureLeft, int textureTop, int textureTopOff, ButtonWidget.PressAction onPress) {
		this(left, top, width, height, LiteralText.EMPTY, texture, textureLeft, textureTop, textureTopOff, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, Text text, ButtonWidget.PressAction onPress) {
		this(left, top, width, height, text, null, 0, 0, 0, "", onPress);
	}

	public GuiButtonGeneral(int left, int top, int width, int height, Text text, Identifier texture, int textureLeft, int textureTop, int textureTopOff, String tooltip, ButtonWidget.PressAction onPress) {
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

		MinecraftClient minecraft = MinecraftClient.getInstance();
		TextRenderer fontRenderer = minecraft.textRenderer;
		if (texture != null) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, texture);
		}
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		//isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		/*RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);*/
		if (hovered && hasGradient)
			fillGradient(matrixStack, x, y, x + width, y + height, 0x80FFFFFF, 0x80FFFFFF);
		if (texture != null)
			drawTexture(matrixStack, x, y, textureLeft / scale, hovered ? (textureTop + textureTopOff) / scale : textureTop / scale, width, height, 256 / scale, 256 / scale);
		//mouseDragged(mc, mouseX, mouseY);
		String displayString = getMessage().getString();
		if (!displayString.equals(""))
			fontRenderer.draw(matrixStack, displayString, x + (width - fontRenderer.getWidth(displayString)) / 2, y - 3 + height / 2, 0x404040);
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
