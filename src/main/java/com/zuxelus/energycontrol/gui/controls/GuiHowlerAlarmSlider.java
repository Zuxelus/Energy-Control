package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiHowlerAlarmSlider extends PressableWidget {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_howler_alarm.png");

	public float sliderValue;
	public boolean dragging;
	private int minValue = 0;
	private int maxValue = 256;
	private int step = 8;
	private TileEntityHowlerAlarm alarm;

	@SuppressWarnings("resource")
	public GuiHowlerAlarmSlider(int x, int y, TileEntityHowlerAlarm alarm) {
		super(x, y, 107, 16, LiteralText.EMPTY);
		this.alarm = alarm;
		dragging = false;
		if (alarm.getWorld().isClient)
			maxValue = ConfigHandler.maxAlarmRange;
		int currentRange = alarm.getRange();
		if (alarm.getWorld().isClient && currentRange > maxValue)
			currentRange = maxValue;
		sliderValue = ((float) currentRange - minValue) / (maxValue - minValue);
		setMessage(new TranslatableText("msg.ec.HowlerAlarmSoundRange", getNormalizedValue()));
	}

	private int getNormalizedValue() {
		return (minValue + (int) Math.floor((maxValue - minValue) * sliderValue)) / step * step;
	}

	@SuppressWarnings("resource")
	private void setSliderPos(double targetX) {
		sliderValue = (float) (targetX - (x + 4)) / (float) (width - 8);
		
		if (sliderValue < 0.0F)
			sliderValue = 0.0F;
		
		if (sliderValue > 1.0F)
			sliderValue = 1.0F;
		
		int newValue = getNormalizedValue();
		if (alarm.getWorld().isClient && alarm.getRange() != newValue) {
			NetworkHelper.updateSeverTileEntity(alarm.getPos(), 2, newValue);
			alarm.setRange(newValue);
		}
		setMessage(new TranslatableText("msg.ec.HowlerAlarmSoundRange", newValue));
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;
		MinecraftClient minecraft = MinecraftClient.getInstance();
		TextRenderer fontRenderer = minecraft.textRenderer;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		if (dragging)
			setSliderPos(mouseX);

		drawTexture(matrixStack, x + (int) (sliderValue * (width - 8)), y, 131, 0, 8, 16);
		fontRenderer.draw(matrixStack, getMessage(), x, y - 12, 0x404040);
	}

	@Override
	public void onPress() { }

	@Override
	public void onClick(double mouseX, double mouseY) {
		setSliderPos(mouseX);
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
