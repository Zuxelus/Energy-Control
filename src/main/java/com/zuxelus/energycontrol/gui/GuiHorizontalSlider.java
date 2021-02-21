package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiHorizontalSlider extends GuiBase {

	private GuiInfoPanel parentGui;
	private TileEntityInfoPanel panel;
	private HorizontalSlider slider;

	public GuiHorizontalSlider(GuiInfoPanel parentGui, TileEntityInfoPanel panel) {
		super("msg.ec.PanelRefreshRate", 152, 64, EnergyControl.MODID + ":textures/gui/gui_horizontal_slider.png");
		this.parentGui = parentGui;
		this.panel = panel;
	}

	@Override
	public void init() {
		super.init();
		slider = new HorizontalSlider(guiLeft + 12, guiTop + 33);
		addButton(slider);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		font.drawString(title.getFormattedText(), (xSize - font.getStringWidth(title.getFormattedText())) / 2, 6, 0x404040);
	}

	@Override
	public void onClose() {
		minecraft.displayGuiScreen(parentGui);
	}

	public class HorizontalSlider extends AbstractButton {
		public int sliderValue;
		public boolean dragging;
		private int minValue = 1;
		private int maxValue = 128;

		public HorizontalSlider(int x, int y) {
			super(x, y, 132, 16, I18n.format("msg.ec.Ticks", Integer.toString(panel.getTickRate())));
			dragging = false;
			sliderValue = panel.getTickRate();
		}

		@SuppressWarnings("resource")
		private void setSliderPos(int targetX) {
			sliderValue = targetX - x + 2;

			if (sliderValue < minValue)
				sliderValue = minValue;
			if (sliderValue > maxValue)
				sliderValue = maxValue;

			if (panel.getWorld().isRemote && panel.getTickRate() != sliderValue) {
				NetworkHelper.updateSeverTileEntity(panel.getPos(), 5, sliderValue);
				panel.setTickRate(sliderValue);
			}
			setMessage(I18n.format("msg.ec.Ticks", Integer.toString(sliderValue)));
		}

		@Override
		public void renderButton(int mouseX, int mouseY, float partialTicks) {
			if (!visible)
				return;
			Minecraft minecraft = Minecraft.getInstance();
			FontRenderer fontRenderer = minecraft.fontRenderer;
			minecraft.getTextureManager().bindTexture(texture);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (dragging)
				setSliderPos(mouseX);

			blit(x - 2 + sliderValue, y, 152, 0, 8, 16);
			fontRenderer.drawString(getMessage(), x - 10 + (width - fontRenderer.getStringWidth(getMessage())) / 2, y - 12, 0x404040);
		}

		@Override
		public void onPress() { }

		@Override
		public void onClick(double mouseX, double mouseY) {
			dragging = true;
		}

		@Override
		public void onRelease(double mouseX, double mouseY) {
			dragging = false;
		}
	}
}
