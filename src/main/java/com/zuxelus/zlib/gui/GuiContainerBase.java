package com.zuxelus.zlib.gui;

import java.text.DecimalFormat;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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

	private final ResourceLocation texture;

	public GuiContainerBase(T container, Inventory inv, Component name, ResourceLocation texture) {
		super(container, inv, name);
		this.texture = texture;
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	public void drawCenteredText(PoseStack matrixStack, Component text, int x, int y) {
		drawCenteredText(matrixStack, text, x, y, 0x404040);
	}

	public void drawRightAlignedText(PoseStack matrixStack, String text, int x, int y) {
		drawRightAlignedText(matrixStack, text, x, y, 0x404040);
	}

	public void drawLeftAlignedText(PoseStack matrixStack, String text, int x, int y) {
		drawLeftAlignedText(matrixStack, text, x, y, 0x404040);
	}

	public void drawCenteredText(PoseStack matrixStack, Component text, int x, int y, int color) {
		FormattedCharSequence ireorderingprocessor = text.getVisualOrderText();
		font.draw(matrixStack, ireorderingprocessor, (x - font.width(ireorderingprocessor)) / 2, y, color);
	}

	public void drawRightAlignedText(PoseStack matrixStack, String text, int x, int y, int color) {
		font.draw(matrixStack, text, x - font.width(text), y, color);
	}

	public void drawLeftAlignedText(PoseStack matrixStack, String text, int x, int y, int color) {
		font.draw(matrixStack, text, x, y, color);
	}

	public void drawRightAlignedGlowingText(PoseStack matrixStack, String text, int x, int y, int color, int glowColor) {
		drawGlowingText(matrixStack, text, x - font.width(text), y, color, glowColor);
	}

	public void drawGlowingText(PoseStack matrixStack, String text, int x, int y, int color, int glowColor) {
		for (int i = 0; i < 4; i++)
			font.draw(matrixStack, text, x + oX[i], y + oY[i], glowColor);
		font.draw(matrixStack, text, x, y, color);
	}

	public void drawCenteredGlowingText(PoseStack matrixStack, String text, int x, int y, int color, int glowColor) {
		drawGlowingText(matrixStack, text, x - font.width(text) / 2, y, color, glowColor);
	}

	public static int multiplyColorComponents(int color, float brightnessFactor) {
		return ((int) (brightnessFactor * (color & MASKR)) & MASKR) | ((int) (brightnessFactor * (color & MASKG)) & MASKG) | ((int) (brightnessFactor * (color & MASKB)) & MASKB);
	}

	protected EditBox addTextFieldWidget(int left, int top, int width, int height, boolean isEnabled, String text) {
		EditBox textBox = new EditBox(font, leftPos + left, topPos + top, width, height, null, TextComponent.EMPTY);
		textBox.setEditable(isEnabled);
		textBox.changeFocus(isEnabled);
		textBox.setValue(text);
		addWidget(textBox);
		setInitialFocus(textBox);
		return textBox;
	}
}
