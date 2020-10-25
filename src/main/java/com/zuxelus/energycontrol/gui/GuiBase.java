package com.zuxelus.energycontrol.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiBase extends GuiScreen {
	protected int xSize = 131;
	protected int ySize = 136;
	protected int guiLeft;
	protected int guiTop;

	protected String name;

	public GuiBase(String name, int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.name = I18n.format(name);
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) guiLeft, (float) guiTop, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
		drawGuiContainerForegroundLayer(mouseX, mouseY);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		RenderHelper.enableStandardItemLighting();
	}

	protected abstract void drawGuiContainerForegroundLayer(int mouseX, int mouseY);

	protected abstract void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
