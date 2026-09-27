package com.zuxelus.zlib.gui;

import java.text.DecimalFormat;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

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

	protected final Identifier texture;

	public GuiContainerBase(T container, Inventory inv, Component name, Identifier texture) {
		this(container, inv, name, texture, 176, 166);
	}

	public GuiContainerBase(T container, Inventory inv, Component name, Identifier texture, int imageWidth, int imageHeight) {
		super(container, inv, name, imageWidth, imageHeight);
		this.texture = texture;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(matrixStack, mouseX, mouseY, partialTicks);
		matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
	}

	public void drawCenteredText(GuiGraphicsExtractor matrixStack, Component text, int x, int y) {
		drawCenteredText(matrixStack, text, x, y, 0x404040);
	}

	public void drawRightAlignedText(GuiGraphicsExtractor matrixStack, String text, int x, int y) {
		drawRightAlignedText(matrixStack, text, x, y, 0x404040);
	}

	public void drawLeftAlignedText(GuiGraphicsExtractor matrixStack, String text, int x, int y) {
		drawLeftAlignedText(matrixStack, text, x, y, 0x404040);
	}

	public void drawCenteredText(GuiGraphicsExtractor matrixStack, Component text, int x, int y, int color) {
		FormattedCharSequence ireorderingprocessor = text.getVisualOrderText();
		matrixStack.text(font, ireorderingprocessor, (x - font.width(ireorderingprocessor)) / 2, y, ARGB.opaque(color), false);
	}

	public void drawRightAlignedText(GuiGraphicsExtractor matrixStack, String text, int x, int y, int color) {
		matrixStack.text(font, text, x - font.width(text), y, ARGB.opaque(color), false);
	}

	public void drawLeftAlignedText(GuiGraphicsExtractor matrixStack, String text, int x, int y, int color) {
		matrixStack.text(font, text, x, y, ARGB.opaque(color), false);
	}

	public void drawRightAlignedGlowingText(GuiGraphicsExtractor matrixStack, String text, int x, int y, int color, int glowColor) {
		drawGlowingText(matrixStack, text, x - font.width(text), y, color, glowColor);
	}

	public void drawGlowingText(GuiGraphicsExtractor matrixStack, String text, int x, int y, int color, int glowColor) {
		for (int i = 0; i < 4; i++)
			matrixStack.text(font, text, x + oX[i], y + oY[i], ARGB.opaque(glowColor), false);
		matrixStack.text(font, text, x, y, ARGB.opaque(color), false);
	}

	public void drawCenteredGlowingText(GuiGraphicsExtractor matrixStack, String text, int x, int y, int color, int glowColor) {
		drawGlowingText(matrixStack, text, x - font.width(text) / 2, y, color, glowColor);
	}

	public static int multiplyColorComponents(int color, float brightnessFactor) {
		return ((int) (brightnessFactor * (color & MASKR)) & MASKR) | ((int) (brightnessFactor * (color & MASKG)) & MASKG) | ((int) (brightnessFactor * (color & MASKB)) & MASKB);
	}

	// While a text field is focused, keys like inventory / drop / hotbar must not act on the container screen
	@Override
	public boolean keyPressed(KeyEvent event) {
		if (!event.isEscape() && isInputCaptured()) {
			GuiEventListener focused = getFocused();
			if (focused != null)
				focused.keyPressed(event);
			return true;
		}
		return super.keyPressed(event);
	}

	protected EditBox addTextFieldWidget(int left, int top, int width, int height, boolean isEnabled, String text) {
		EditBox textBox = new EditBox(font, leftPos + left, topPos + top, width, height, null, CommonComponents.EMPTY);
		textBox.setEditable(isEnabled);
		textBox.setValue(text);
		addWidget(textBox);
		setInitialFocus(textBox);
		return textBox;
	}
}
