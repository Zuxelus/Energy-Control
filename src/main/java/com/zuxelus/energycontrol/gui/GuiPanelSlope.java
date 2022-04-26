package com.zuxelus.energycontrol.gui;

import java.io.IOException;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPanelSlope extends GuiBase {
	private GuiPanelBase parentGui;
	private TileEntityAdvancedInfoPanel panel;

	public GuiPanelSlope(GuiPanelBase parentGui, TileEntityAdvancedInfoPanel panel) {
		super("", 171, 94, EnergyControl.MODID + ":textures/gui/gui_slope.png");
		this.parentGui = parentGui;
		this.panel = panel;
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
	public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		int textureHeight = 4 * (16 - panel.thickness);

		drawTexturedModalRect(guiLeft + 21, guiTop + 25, 172, 0, 14, textureHeight);
		drawTexturedModalRect(guiLeft + 79, guiTop + 25 + (panel.rotateHor < 0 ? 32 + panel.rotateHor * 4 / 7 : 32), 186, 0, 14, Math.abs(panel.rotateHor * 4 / 7));
		drawTexturedModalRect(guiLeft + 137, guiTop + 25 + (panel.rotateVert < 0 ? 32 + panel.rotateVert * 4 / 7 : 32), 186, 0, 14, Math.abs(panel.rotateVert * 4 / 7));
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1)
			FMLClientHandler.instance().getClient().displayGuiScreen(parentGui);
		else
			super.keyTyped(typedChar, keyCode);
	}
}
