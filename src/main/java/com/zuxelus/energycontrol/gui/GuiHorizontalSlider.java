package com.zuxelus.energycontrol.gui;

import java.io.IOException;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHorizontalSlider extends GuiBase {
	private GuiPanelBase parentGui;
	private TileEntityInfoPanel panel;
	private HorizontalSlider slider;

	public GuiHorizontalSlider(GuiPanelBase parentGui, TileEntityInfoPanel panel) {
		super("msg.ec.PanelRefreshRate", 152, 64, EnergyControl.MODID + ":textures/gui/gui_horizontal_slider.png");
		this.parentGui = parentGui;
		this.panel = panel;
	}

	@Override
	public void initGui() {
		super.initGui();
		slider = new HorizontalSlider(1, guiLeft + 12, guiTop + 33);
		addButton(slider);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(name, (xSize - fontRendererObj.getStringWidth(name)) / 2, 6, 0x404040);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1)
			FMLClientHandler.instance().getClient().displayGuiScreen(parentGui);
		else
			super.keyTyped(typedChar, keyCode);
	}

	public class HorizontalSlider extends GuiButton {
		public int sliderValue;
		public boolean dragging;
		private int minValue = 1;
		private int maxValue = 128;

		public HorizontalSlider(int id, int x, int y) {
			super(id, x, y, 132, 16, I18n.format("msg.ec.Ticks", Integer.toString(panel.getTickRate())));
			dragging = false;
			sliderValue = panel.getTickRate();
		}

		private void setSliderPos(int targetX) {
			sliderValue = targetX - xPosition + 2;

			if (sliderValue < minValue)
				sliderValue = minValue;
			if (sliderValue > maxValue)
				sliderValue = maxValue;

			if (panel.getWorld().isRemote && panel.getTickRate() != sliderValue) {
				NetworkHelper.updateSeverTileEntity(panel.getPos(), 5, sliderValue);
				panel.setTickRate(sliderValue);
			}
			displayString = I18n.format("msg.ec.Ticks", Integer.toString(sliderValue));
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (!visible)
				return;
			mc.getTextureManager().bindTexture(texture);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			if (dragging)
				setSliderPos(mouseX);

			drawTexturedModalRect(xPosition - 2 + sliderValue, yPosition, 152, 0, 8, 16);
			fontRendererObj.drawString(displayString, xPosition - 10 + (xSize - fontRendererObj.getStringWidth(displayString)) / 2, yPosition - 12, 0x404040);
		}

		@Override
		public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
			if (!super.mousePressed(mc, mouseX, mouseY))
				return false;
			dragging = true;
			return true;
		}

		@Override
		public void mouseReleased(int mouseX, int mouseY) {
			super.mouseReleased(mouseX, mouseY);
			dragging = false;
		}
	}
}
