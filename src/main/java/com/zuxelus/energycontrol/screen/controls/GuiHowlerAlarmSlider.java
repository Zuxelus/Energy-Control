package com.zuxelus.energycontrol.screen.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.blockentities.HowlerAlarmBlockEntity;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.network.NetworkHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiHowlerAlarmSlider extends AbstractPressableButtonWidget {
	private static final Identifier TEXTURE = new Identifier("energycontrol:textures/gui/gui_howler_alarm.png");

	public float sliderValue;
	public boolean dragging;
	private int minValue = 0;
	private int maxValue = 256;
	private int step = 8;
	private HowlerAlarmBlockEntity alarm;

	public GuiHowlerAlarmSlider(int id, int x, int y, HowlerAlarmBlockEntity alarm) {
		super(x, y, 107, 16, new LiteralText(""));
		this.alarm = alarm;
		dragging = false;
		if (alarm.getWorld().isClient)
			maxValue = ConfigHandler.maxAlarmRange;
		int currentRange = alarm.getRange();
		if (alarm.getWorld().isClient && currentRange > maxValue)
			currentRange = maxValue;
		sliderValue = ((float) currentRange - minValue) / (maxValue - minValue);
		setMessage(new LiteralText(I18n.translate("msg.ec.HowlerAlarmSoundRange", getNormalizedValue())));
	}

	private int getNormalizedValue() {
		return (minValue + (int) Math.floor((maxValue - minValue) * sliderValue)) / step * step;
	}

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
		setMessage(new LiteralText(I18n.translate("msg.ec.HowlerAlarmSoundRange", newValue)));
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		if (dragging)
			setSliderPos(mouseX);
		
		drawTexture(matrices, x + (int) (sliderValue * (width - 8)), y, 131, 0, 8, 16);
		mc.textRenderer.draw(matrices, getMessage(), x, y - 12, 0x404040);
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		setSliderPos(mouseX);
		dragging = true;
	}

	@Override
	public void onPress() { }

	@Override
	public void onRelease(double mouseX, double mouseY) {
		dragging = false;
	}
}
