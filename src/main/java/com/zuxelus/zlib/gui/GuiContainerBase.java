package com.zuxelus.zlib.gui;

import java.text.DecimalFormat;
import net.minecraft.client.renderer.RenderPipelines;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Environment(EnvType.CLIENT)
public class GuiContainerBase<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
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

	public GuiContainerBase(T container, Inventory inv, Component name, Identifier texture) {
		this(container, inv, name, texture, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
	}

	// the image size is final now and must be known when the label positions are computed
	public GuiContainerBase(T container, Inventory inv, Component name, Identifier texture, int imageWidth, int imageHeight) {
		super(container, inv, name, imageWidth, imageHeight);
		this.texture = texture;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(context, mouseX, mouseY, partialTicks);
		context.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
	}

	public void drawCenteredText(GuiGraphicsExtractor context, Component text, int x, int y) {
		drawCenteredText(context, text, x, y, 0xFF404040);
	}

	public void drawRightAlignedText(GuiGraphicsExtractor context, String text, int x, int y) {
		drawRightAlignedText(context, text, x, y, 0xFF404040);
	}

	public void drawLeftAlignedText(GuiGraphicsExtractor context, String text, int x, int y) {
		drawLeftAlignedText(context, text, x, y, 0xFF404040);
	}

	public void drawCenteredText(GuiGraphicsExtractor context, Component text, int x, int y, int color) {
		FormattedCharSequence ireorderingprocessor = text.getVisualOrderText();
		context.text(font, ireorderingprocessor, (x - font.width(ireorderingprocessor)) / 2, y, ARGB.opaque(color), false);
	}

	public void drawRightAlignedText(GuiGraphicsExtractor context, String text, int x, int y, int color) {
		context.text(font, text, x - font.width(text), y, ARGB.opaque(color), false);
	}

	public void drawLeftAlignedText(GuiGraphicsExtractor context, String text, int x, int y, int color) {
		context.text(font, text, x, y, ARGB.opaque(color), false);
	}

	public void drawRightAlignedGlowingText(GuiGraphicsExtractor context, String text, int x, int y, int color, int glowColor) {
		drawGlowingText(context, text, x - font.width(text), y, color, glowColor);
	}

	public void drawGlowingText(GuiGraphicsExtractor context, String text, int x, int y, int color, int glowColor) {
		for (int i = 0; i < 4; i++)
			context.text(font, text, x + oX[i], y + oY[i], ARGB.opaque(glowColor), false);
		context.text(font, text, x, y, ARGB.opaque(color), false);
	}

	public void drawCenteredGlowingText(GuiGraphicsExtractor context, String text, int x, int y, int color, int glowColor) {
		drawGlowingText(context, text, x - font.width(text) / 2, y, color, glowColor);
	}

	public static int multiplyColorComponents(int color, float brightnessFactor) {
		return ((int) (brightnessFactor * (color & MASKR)) & MASKR) | ((int) (brightnessFactor * (color & MASKG)) & MASKG) | ((int) (brightnessFactor * (color & MASKB)) & MASKB);
	}

	protected EditBox addTextFieldWidget(int left, int top, int width, int height, boolean isEnabled, String text) {
		EditBox textBox = new EditBox(font, leftPos + left, topPos + top, width, height, null, CommonComponents.EMPTY);
		textBox.setEditable(isEnabled);
		textBox.setFocused(isEnabled);
		textBox.setValue(text);
		addWidget(textBox);
		setInitialFocus(textBox);
		return textBox;
	}
}
