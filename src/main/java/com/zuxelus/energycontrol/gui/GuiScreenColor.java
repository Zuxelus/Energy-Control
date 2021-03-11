package com.zuxelus.energycontrol.gui;

import java.io.IOException;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenColor extends GuiBase {

	private GuiInfoPanel parentGui;
	private int colorText;
	private int colorBack;
	private TileEntityInfoPanel panel;

	public GuiScreenColor(GuiInfoPanel parentGui, TileEntityInfoPanel panel) {
		super("", 234, 94, EnergyControl.MODID + ":textures/gui/gui_colors.png");
		this.parentGui = parentGui;
		this.panel = panel;
		colorBack = panel.getColorBackground();
		colorText = panel.getColorText();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawTexturedModalRect(5 + colorBack * 14, 30, 234, 0, 14, 14);
		drawTexturedModalRect(5 + colorText * 14, 61, 234, 0, 14, 14);
		fontRenderer.drawString(I18n.format("msg.ec.ScreenColor"), 8, 20, 0x404040);
		fontRenderer.drawString(I18n.format("msg.ec.TextColor"), 8, 52, 0x404040);
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
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1)
			FMLClientHandler.instance().getClient().displayGuiScreen(parentGui);
		else
			super.keyTyped(typedChar, keyCode);
	}
}
