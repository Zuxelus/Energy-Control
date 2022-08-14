package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
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
	protected void drawGuiContainerForegroundLayer(PoseStack matrixStack, int mouseX, int mouseY) {
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
			sliderValue = targetX - x + 2;

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
		public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			if (!visible)
				return;
			Minecraft minecraft = Minecraft.getInstance();
			Font fontRenderer = minecraft.font;
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, texture);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			if (dragging)
				setSliderPos(mouseX);

			blit(matrixStack, x - 2 + sliderValue, y, 152, 0, 8, 16);
			FormattedCharSequence ireorderingprocessor = getMessage().getVisualOrderText();
			fontRenderer.draw(matrixStack, ireorderingprocessor, x - 10 + (width - fontRenderer.width(ireorderingprocessor)) / 2, y - 12, 0x404040);
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
		public void updateNarration(NarrationElementOutput output) {
			// TODO Auto-generated method stub
		}
	}
}
