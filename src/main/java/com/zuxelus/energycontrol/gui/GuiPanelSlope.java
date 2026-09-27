package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;

public class GuiPanelSlope extends GuiBase {
	private GuiPanelBase<?> parentGui;
	private TileEntityAdvancedInfoPanel panel;

	public GuiPanelSlope(GuiPanelBase<?> parentGui, TileEntityAdvancedInfoPanel panel) {
		super("", 171, 94, EnergyControl.MODID + ":textures/gui/gui_slope.png");
		this.parentGui = parentGui;
		this.panel = panel;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mouseX = event.x() - guiLeft;
		double mouseY = event.y() - guiTop;
		if (mouseY >= 23 && mouseY <= 89) {
			int amount = (int) ((87 - mouseY + 2) / 4);
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
			NetworkHelper.updateSeverTileEntity(panel.getBlockPos(), 10, offset + amount);
			panel.setValues(offset + amount);
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(GuiGraphicsExtractor matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		int textureHeight = 4 * (16 - panel.thickness);

		matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, guiLeft + 21, guiTop + 25, 172, 0, 14, textureHeight, 256, 256);
		matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, guiLeft + 79, guiTop + 25 + (panel.rotateHor < 0 ? 32 + panel.rotateHor * 4 / 7 : 32), 186, 0, 14, Math.abs(panel.rotateHor * 4 / 7), 256, 256);
		matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, guiLeft + 137, guiTop + 25 + (panel.rotateVert < 0 ? 32 + panel.rotateVert * 4 / 7 : 32), 186, 0, 14, Math.abs(panel.rotateVert * 4 / 7), 256, 256);
	}

	@Override
	public void onClose() {
		minecraft.gui.setScreen(parentGui);
	}
}
