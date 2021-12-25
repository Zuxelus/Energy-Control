package com.zuxelus.zlib.gui;

import java.text.DecimalFormat;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiContainerBase<T extends ScreenHandler> extends HandledScreen<T> {
	private static final int oX[] = {0, -1, 0, 1};
	private static final int oY[] = {-1, 0, 1, 0};
	private static final int MASKR = 0xFF0000;
	private static final int MASKG = 0x00FF00;
	private static final int MASKB = 0x0000FF;
	protected static final int GREEN = 0x55FF55;
	protected static final int RED = 0xFF5555;
	protected static final int GREENGLOW = multiplyColorComponents(GREEN, 0.16F);
	protected static final int REDGLOW = multiplyColorComponents(RED, 0.16F);
	protected DecimalFormat fraction = new DecimalFormat("##0.00");

	private final Identifier texture;

	public GuiContainerBase(T container, PlayerInventory inv, Text name, Identifier texture) {
		super(container, inv, name);
		this.texture = texture;
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexture(matrixStack, x, y, 0, 0, backgroundWidth, backgroundHeight);
	}

	public void drawCenteredText(MatrixStack matrixStack, Text text, int x, int y) {
		drawCenteredText(matrixStack, text, x, y, 0x404040);
	}

	public void drawRightAlignedText(MatrixStack matrixStack, String text, int x, int y) {
		drawRightAlignedText(matrixStack, text, x, y, 0x404040);
	}

	public void drawLeftAlignedText(MatrixStack matrixStack, String text, int x, int y) {
		drawLeftAlignedText(matrixStack, text, x, y, 0x404040);
	}

	public void drawCenteredText(MatrixStack matrixStack, Text text, int x, int y, int color) {
		OrderedText ireorderingprocessor = text.asOrderedText();
		textRenderer.draw(matrixStack, ireorderingprocessor, (x - textRenderer.getWidth(ireorderingprocessor)) / 2, y, color);
	}

	public void drawRightAlignedText(MatrixStack matrixStack, String text, int x, int y, int color) {
		textRenderer.draw(matrixStack, text, x - textRenderer.getWidth(text), y, color);
	}

	public void drawLeftAlignedText(MatrixStack matrixStack, String text, int x, int y, int color) {
		textRenderer.draw(matrixStack, text, x, y, color);
	}

	public void drawRightAlignedGlowingText(MatrixStack matrixStack, String text, int x, int y, int color, int glowColor) {
		drawGlowingText(matrixStack, text, x - textRenderer.getWidth(text), y, color, glowColor);
	}

	public void drawGlowingText(MatrixStack matrixStack, String text, int x, int y, int color, int glowColor) {
		for (int i = 0; i < 4; i++)
			textRenderer.draw(matrixStack, text, x + oX[i], y + oY[i], glowColor);
		textRenderer.draw(matrixStack, text, x, y, color);
	}

	public void drawCenteredGlowingText(MatrixStack matrixStack, String text, int x, int y, int color, int glowColor) {
		drawGlowingText(matrixStack, text, x - textRenderer.getWidth(text) / 2, y, color, glowColor);
	}

	public static int multiplyColorComponents(int color, float brightnessFactor) {
		return ((int) (brightnessFactor * (color & MASKR)) & MASKR) | ((int) (brightnessFactor * (color & MASKG)) & MASKG) | ((int) (brightnessFactor * (color & MASKB)) & MASKB);
	}

	protected TextFieldWidget addTextFieldWidget(int left, int top, int width, int height, boolean isEnabled, String text) {
		TextFieldWidget textBox = new TextFieldWidget(textRenderer, x + left, y + top, width, height, null, LiteralText.EMPTY);
		textBox.setEditable(isEnabled);
		textBox.changeFocus(isEnabled);
		textBox.setText(text);
		addSelectableChild(textBox);
		setInitialFocus(textBox);
		return textBox;
	}
}
