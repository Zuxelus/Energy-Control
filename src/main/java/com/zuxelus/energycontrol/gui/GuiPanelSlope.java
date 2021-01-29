package com.zuxelus.energycontrol.gui;

import java.io.IOException;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPanelSlope extends GuiScreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/gui/gui_slope.png");

	protected int xSize = 171;
	protected int ySize = 94;
	protected int guiLeft;
	protected int guiTop;

	private GuiInfoPanel parentGui;
	private TileEntityAdvancedInfoPanel panel;

	public GuiPanelSlope(GuiInfoPanel parentGui, TileEntityAdvancedInfoPanel panel) {
		this.parentGui = parentGui;
		this.panel = panel;
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
		if (mouseY >= 23 && mouseY <= 89) {
			int amount = (87 - mouseY + 2) / 4;
			int offset = 0;
			if (mouseX >= 21 && mouseX <= 34) {
				offset = TileEntityAdvancedInfoPanel.OFFSET_THICKNESS;
				if (amount < 1)
					amount = 1;
			} else if (mouseX >= 79 && mouseX <= 92) {
				offset = TileEntityAdvancedInfoPanel.OFFSET_ROTATE_HOR;
				if (amount < 0)
					amount = 0;
			} else if (mouseX >= 137 && mouseX <= 150) {
				offset = TileEntityAdvancedInfoPanel.OFFSET_ROTATE_VERT;
				if (amount < 0)
					amount = 0;
			}
			NetworkHelper.updateSeverTileEntity(panel.getPos(), 10, offset + amount);
			panel.setValues(offset + amount);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		int textureHeight = 4 * (16 - panel.thickness);

		drawTexturedModalRect(left + 21, top + 25, 172, 0, 14, textureHeight);
		drawTexturedModalRect(left + 79, top + 25 + (panel.rotateHor < 0 ? 32 + panel.rotateHor * 4 / 7 : 32), 186, 0, 14, Math.abs(panel.rotateHor * 4 / 7));
		drawTexturedModalRect(left + 137, top + 25 + (panel.rotateVert < 0 ? 32 + panel.rotateVert * 4 / 7 : 32), 186, 0, 14, Math.abs(panel.rotateVert * 4 / 7));

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1)
			FMLClientHandler.instance().getClient().displayGuiScreen(parentGui);
		else
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
