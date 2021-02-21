package com.zuxelus.zlib.gui;

import java.text.DecimalFormat;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiContainerBase<T extends Container> extends ContainerScreen<T> {
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

	protected final ResourceLocation texture;
	protected String name;

	public GuiContainerBase(T container, PlayerInventory inv, String name, String texture) {
		super(container, inv, new TranslationTextComponent(name));
		this.name = I18n.format(name);
		this.texture = new ResourceLocation(texture);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(texture);
		blit(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	public void drawCenteredText(String text, int x, int y) {
		drawCenteredText(text, x, y, 0x404040);
	}

	public void drawRightAlignedText(String text, int x, int y) {
		drawRightAlignedText(text, x, y, 0x404040);
	}

	public void drawLeftAlignedText(String text, int x, int y) {
		drawLeftAlignedText(text, x, y, 0x404040);
	}

	public void drawCenteredText(String text, int x, int y, int color) {
		font.drawString(text, (x - font.getStringWidth(text)) / 2, y, color);
	}

	public void drawRightAlignedText(String text, int x, int y, int color) {
		font.drawString(text, x - font.getStringWidth(text), y, color);
	}

	public void drawLeftAlignedText(String text, int x, int y, int color) {
		font.drawString(text, x, y, color);
	}

	public void drawRightAlignedGlowingText(String text, int x, int y, int color, int glowColor) {
		drawGlowingText(text, x - font.getStringWidth(text), y, color, glowColor);
	}

	public void drawGlowingText(String text, int x, int y, int color, int glowColor) {
		for (int i = 0; i < 4; i++)
			font.drawString(text, x + oX[i], y + oY[i], glowColor);
		font.drawString(text, x, y, color);
	}

	public void drawCenteredGlowingText(String text, int x, int y, int color, int glowColor) {
		drawGlowingText(text, x - font.getStringWidth(text) / 2, y, color, glowColor);
	}

	public static int multiplyColorComponents(int color, float brightnessFactor) {
		return ((int) (brightnessFactor * (color & MASKR)) & MASKR) | ((int) (brightnessFactor * (color & MASKG)) & MASKG) | ((int) (brightnessFactor * (color & MASKB)) & MASKB);
	}
}
