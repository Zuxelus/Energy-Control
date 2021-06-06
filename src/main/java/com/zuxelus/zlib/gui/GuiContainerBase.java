package com.zuxelus.zlib.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;

@SideOnly(Side.CLIENT)
public class GuiContainerBase extends GuiContainer {
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
	protected String name;

	public GuiContainerBase(Container container, String name, ResourceLocation texture) {
		super(container);
		this.name = I18n.format(name);
		this.texture = texture;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
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
		fontRenderer.drawString(text, (x - fontRenderer.getStringWidth(text)) / 2, y, color);
	}

	public void drawRightAlignedText(String text, int x, int y, int color) {
		fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text), y, color);
	}

	public void drawLeftAlignedText(String text, int x, int y, int color) {
		fontRenderer.drawString(text, x, y, color);
	}

	public void drawRightAlignedGlowingText(String text, int x, int y, int color, int glowColor) {
		drawGlowingText(text, x - fontRenderer.getStringWidth(text), y, color, glowColor);
	}

	public void drawGlowingText(String text, int x, int y, int color, int glowColor) {
		for (int i = 0; i < 4; i++)
			fontRenderer.drawString(text, x + oX[i], y + oY[i], glowColor);
		fontRenderer.drawString(text, x, y, color);
	}

	public void drawCenteredGlowingText(String text, int x, int y, int color, int glowColor) {
		drawGlowingText(text, x - fontRenderer.getStringWidth(text) / 2, y, color, glowColor);
	}

	public static int multiplyColorComponents(int color, float brightnessFactor) {
		return ((int) (brightnessFactor * (color & MASKR)) & MASKR) | ((int) (brightnessFactor * (color & MASKG)) & MASKG) | ((int) (brightnessFactor * (color & MASKB)) & MASKB);
	}
}
