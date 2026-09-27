package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

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
	protected void drawGuiContainerForegroundLayer(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {
		drawTitle(matrixStack);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == InputConstants.KEY_ESCAPE) {
			minecraft.gui.setScreen(parentGui);
			return true;
		}
		return super.keyPressed(event);
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

			if (panel.getLevel().isClientSide() && panel.getTickRate() != sliderValue) {
				NetworkHelper.updateSeverTileEntity(panel.getBlockPos(), 5, sliderValue);
				panel.setTickRate(sliderValue);
			}
			setMessage(Component.translatable("msg.ec.Ticks", Integer.toString(sliderValue)));
		}

		@Override
		protected void extractContents(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
			if (!visible)
				return;
			if (dragging)
				setSliderPos(mouseX);

			matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, getX() - 2 + sliderValue, getY(), 152, 0, 8, 16, 256, 256);
			FormattedCharSequence ireorderingprocessor = getMessage().getVisualOrderText();
			Minecraft minecraft = Minecraft.getInstance();
			Font fontRenderer = minecraft.font;
			matrixStack.text(fontRenderer, ireorderingprocessor, getX() - 10 + (width - fontRenderer.width(ireorderingprocessor)) / 2, getY() - 12, ARGB.opaque(0x404040), false);
		}

		@Override
		public void onPress(InputWithModifiers input) { }

		@Override
		public void onClick(MouseButtonEvent event, boolean doubleClick) {
			dragging = true;
		}

		@Override
		public void onRelease(MouseButtonEvent event) {
			dragging = false;
		}

		@Override
		protected void updateWidgetNarration(NarrationElementOutput output) {
			// TODO Auto-generated method stub
			
		}
	}
}
