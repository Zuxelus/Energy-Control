package com.zuxelus.energycontrol.gui;

import java.io.IOException;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuiScreenColor extends GuiScreen {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_colors.png");

	private GuiInfoPanel parentGui;

	protected int xSize = 234;
	protected int ySize = 94;
	protected int guiLeft;
	protected int guiTop;
	private int colorText;
	private int colorBack;
	private TileEntityInfoPanel panel;

	public GuiScreenColor(GuiInfoPanel parentGui, TileEntityInfoPanel panel) {
		this.parentGui = parentGui;
		this.panel = panel;
		colorBack = panel.getColorBackground();
		colorText = panel.getColorText();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		mouseX -= guiLeft;
		mouseY -= guiTop;
		if (mouseX >= 7 && mouseX <= 226) {
			int index = (mouseX - 7) / 14;
			int shift = (mouseX - 7) % 14;
			if (mouseY >= 32 && mouseY <= 41 && shift <= 9) {// back
				colorBack = index;
				NetworkHelper.updateSeverTileEntity(panel.getPos(), 2, (colorBack << 4) | colorText);
				panel.setColorBackground(colorBack);
			} else if (mouseY >= 63 && mouseY <= 72 && shift <= 9) {// /text
				colorText = index;
				NetworkHelper.updateSeverTileEntity(panel.getPos(), 2, (colorBack << 4) | colorText);
				panel.setColorText(colorText);
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		mc.getTextureManager().bindTexture(TEXTURE_LOCATION);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		drawTexturedModalRect(left + 5 + colorBack * 14, top + 30, 234, 0, 14, 14);
		drawTexturedModalRect(left + 5 + colorText * 14, top + 61, 234, 0, 14, 14);
		fontRendererObj.drawString(I18n.format("msg.ec.ScreenColor"), guiLeft + 8, guiTop + 20, 0x404040);
		fontRendererObj.drawString(I18n.format("msg.ec.TextColor"), guiLeft + 8, guiTop + 52, 0x404040);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1) {
			parentGui.isColored = !panel.getColored();
			FMLClientHandler.instance().getClient().displayGuiScreen(parentGui);
		} else
			super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		buttonList.clear();
	}
}
