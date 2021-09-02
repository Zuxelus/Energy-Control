package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;
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
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawTitle(matrixStack);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			minecraft.displayGuiScreen(parentGui);
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
			super(x, y, 132, 16, new TranslationTextComponent("msg.ec.Ticks", Integer.toString(panel.getTickRate())));
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
			setMessage(new TranslationTextComponent("msg.ec.Ticks", Integer.toString(sliderValue)));
		}

		@Override
		public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			if (!visible)
				return;
			Minecraft minecraft = Minecraft.getInstance();
			FontRenderer fontRenderer = minecraft.fontRenderer;
			minecraft.getTextureManager().bindTexture(texture);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (dragging)
				setSliderPos(mouseX);

			blit(matrixStack, x - 2 + sliderValue, y, 152, 0, 8, 16);
			IReorderingProcessor ireorderingprocessor = getMessage().func_241878_f();
			fontRenderer.func_238422_b_(matrixStack, ireorderingprocessor, x - 10 + (width - fontRenderer.func_243245_a(ireorderingprocessor)) / 2, y - 12, 0x404040);
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
