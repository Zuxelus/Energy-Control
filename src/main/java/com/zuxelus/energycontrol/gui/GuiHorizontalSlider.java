package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
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
		addDrawableChild(slider);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawTitle(matrixStack);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			client.setScreen(parentGui);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public class HorizontalSlider extends PressableWidget {
		public int sliderValue;
		public boolean dragging;
		private int minValue = 1;
		private int maxValue = 128;

		public HorizontalSlider(int x, int y) {
			super(x, y, 132, 16, new TranslatableText("msg.ec.Ticks", Integer.toString(panel.getTickRate())));
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

			if (panel.getWorld().isClient && panel.getTickRate() != sliderValue) {
				NetworkHelper.updateSeverTileEntity(panel.getPos(), 5, sliderValue);
				panel.setTickRate(sliderValue);
			}
			setMessage(new TranslatableText("msg.ec.Ticks", Integer.toString(sliderValue)));
		}

		@Override
		public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			if (!visible)
				return;
			MinecraftClient minecraft = MinecraftClient.getInstance();
			TextRenderer fontRenderer = minecraft.textRenderer;
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, texture);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			if (dragging)
				setSliderPos(mouseX);

			drawTexture(matrixStack, x - 2 + sliderValue, y, 152, 0, 8, 16);
			OrderedText ireorderingprocessor = getMessage().asOrderedText();
			fontRenderer.draw(matrixStack, ireorderingprocessor, x - 10 + (width - fontRenderer.getWidth(ireorderingprocessor)) / 2, y - 12, 0x404040);
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
		public void appendNarrations(NarrationMessageBuilder var1) {
			// TODO Auto-generated method stub
		}
	}
}
