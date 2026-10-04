package com.zuxelus.zlib.gui;

import net.minecraft.screen.ScreenTexts;

import java.text.DecimalFormat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
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
	protected void drawBackground(DrawContext context, float partialTicks, int mouseX, int mouseY) {
		context.drawTexture(texture, x, y, 0, 0, backgroundWidth, backgroundHeight);
	}

	public void drawCenteredText(DrawContext context, Text text, int x, int y) {
		drawCenteredText(context, text, x, y, 0x404040);
	}

	public void drawRightAlignedText(DrawContext context, String text, int x, int y) {
		drawRightAlignedText(context, text, x, y, 0x404040);
	}

	public void drawLeftAlignedText(DrawContext context, String text, int x, int y) {
		drawLeftAlignedText(context, text, x, y, 0x404040);
	}

	public void drawCenteredText(DrawContext context, Text text, int x, int y, int color) {
		OrderedText ireorderingprocessor = text.asOrderedText();
		context.drawText(textRenderer, ireorderingprocessor, (x - textRenderer.getWidth(ireorderingprocessor)) / 2, y, color, false);
	}

	public void drawRightAlignedText(DrawContext context, String text, int x, int y, int color) {
		context.drawText(textRenderer, text, x - textRenderer.getWidth(text), y, color, false);
	}

	public void drawLeftAlignedText(DrawContext context, String text, int x, int y, int color) {
		context.drawText(textRenderer, text, x, y, color, false);
	}

	public void drawRightAlignedGlowingText(DrawContext context, String text, int x, int y, int color, int glowColor) {
		drawGlowingText(context, text, x - textRenderer.getWidth(text), y, color, glowColor);
	}

	public void drawGlowingText(DrawContext context, String text, int x, int y, int color, int glowColor) {
		for (int i = 0; i < 4; i++)
			context.drawText(textRenderer, text, x + oX[i], y + oY[i], glowColor, false);
		context.drawText(textRenderer, text, x, y, color, false);
	}

	public void drawCenteredGlowingText(DrawContext context, String text, int x, int y, int color, int glowColor) {
		drawGlowingText(context, text, x - textRenderer.getWidth(text) / 2, y, color, glowColor);
	}

	public static int multiplyColorComponents(int color, float brightnessFactor) {
		return ((int) (brightnessFactor * (color & MASKR)) & MASKR) | ((int) (brightnessFactor * (color & MASKG)) & MASKG) | ((int) (brightnessFactor * (color & MASKB)) & MASKB);
	}

	protected TextFieldWidget addTextFieldWidget(int left, int top, int width, int height, boolean isEnabled, String text) {
		TextFieldWidget textBox = new TextFieldWidget(textRenderer, x + left, y + top, width, height, null, ScreenTexts.EMPTY);
		textBox.setEditable(isEnabled);
		textBox.setFocused(isEnabled);
		textBox.setText(text);
		addSelectableChild(textBox);
		setInitialFocus(textBox);
		return textBox;
	}
}
