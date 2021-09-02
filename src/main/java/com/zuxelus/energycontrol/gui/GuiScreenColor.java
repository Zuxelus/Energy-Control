package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		blit(matrixStack, 5 + colorBack * 14, 30, 234, 0, 14, 14);
		blit(matrixStack, 5 + colorText * 14, 61, 234, 0, 14, 14);
		font.func_243248_b(matrixStack, new TranslationTextComponent("msg.ec.ScreenColor"), 8, 20, 0x404040);
		font.func_243248_b(matrixStack, new TranslationTextComponent("msg.ec.TextColor"), 8, 52, 0x404040);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		mouseX -= guiLeft;
		mouseY -= guiTop;
		if (mouseX >= 7 && mouseX <= 226) {
			int index = ((int) mouseX - 7) / 14;
			int shift = ((int) mouseX - 7) % 14;
			if (mouseY >= 32 && mouseY <= 41 && shift <= 9) {// back
				colorBack = index;
				NetworkHelper.updateSeverTileEntity(panel.getPos(), 2, (colorBack << 4) | colorText);
				panel.setColorBackground(colorBack);
			} else if (mouseY >= 63 && mouseY <= 72 && shift <= 9) {// /text
				colorText = index;
				NetworkHelper.updateSeverTileEntity(panel.getPos(), 2, (colorBack << 4) | colorText);
				panel.setColorText(colorText);
			}
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			minecraft.displayGuiScreen(parentGui);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
