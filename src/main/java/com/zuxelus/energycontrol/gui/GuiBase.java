package com.zuxelus.energycontrol.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;

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
		guiLeft = (this.width - xSize) / 2;
		guiTop = (this.height - ySize) / 2;
		buttonList.clear();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		int i = this.guiLeft;
		int j = this.guiTop;
		drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslatef((float) i, (float) j, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
	}

	protected abstract void drawGuiContainerForegroundLayer(int mouseX, int mouseY);

	protected abstract void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
