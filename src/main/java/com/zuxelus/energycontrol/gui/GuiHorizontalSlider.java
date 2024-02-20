package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiHorizontalSlider extends GuiBase {

	private GuiPanelBase<?> parentGui;
	private TileEntityInfoPanel panel;
	private HorizontalSlider slider;

	public GuiHorizontalSlider(GuiPanelBase<?> parentGui, TileEntityInfoPanel panel) {
		super("msg.ec.PanelRefreshRate", 152, 64, EnergyControl.MODID + ":textures/gui/gui_horizontal_slider.png");
		this.parentGui = parentGui;
		this.panel = panel;
	}

	@Override
	public void init() {
		super.init();
		slider = new HorizontalSlider(guiLeft + 12, guiTop + 33);
		addRenderableWidget(slider);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphics matrixStack, int mouseX, int mouseY) {
		drawTitle(matrixStack);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			minecraft.setScreen(parentGui);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public class HorizontalSlider extends AbstractButton {
		public int sliderValue;
		public boolean dragging;
		private int minValue = 1;
		private int maxValue = 128;

		public HorizontalSlider(int x, int y) {
			super(x, y, 132, 16, Component.translatable("msg.ec.Ticks", Integer.toString(panel.getTickRate())));
			dragging = false;
			sliderValue = panel.getTickRate();
		}

		@SuppressWarnings("resource")
		private void setSliderPos(int targetX) {
			sliderValue = targetX - getX() + 2;

			if (sliderValue < minValue)
				sliderValue = minValue;
			if (sliderValue > maxValue)
				sliderValue = maxValue;

			if (panel.getLevel().isClientSide && panel.getTickRate() != sliderValue) {
				NetworkHelper.updateSeverTileEntity(panel.getBlockPos(), 5, sliderValue);
				panel.setTickRate(sliderValue);
			}
			setMessage(Component.translatable("msg.ec.Ticks", Integer.toString(sliderValue)));
		}

		@Override
		protected void renderWidget(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
			if (!visible)
				return;
			if (dragging)
				setSliderPos(mouseX);

			matrixStack.blit(texture, getX() - 2 + sliderValue, getY(), 152, 0, 8, 16);
			FormattedCharSequence ireorderingprocessor = getMessage().getVisualOrderText();
			Minecraft minecraft = Minecraft.getInstance();
			Font fontRenderer = minecraft.font;
			matrixStack.drawString(fontRenderer, ireorderingprocessor, getX() - 10 + (width - fontRenderer.width(ireorderingprocessor)) / 2, getY() - 12, 0x404040, false);
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

		@Override
		protected void updateWidgetNarration(NarrationElementOutput output) {
			// TODO Auto-generated method stub
			
		}
	}
}
