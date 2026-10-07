package com.zuxelus.energycontrol.gui.controls;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class GuiHowlerAlarmSlider extends AbstractButton {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/gui/gui_howler_alarm.png");

	public float sliderValue;
	public boolean dragging;
	private int minValue = 0;
	private int maxValue = 256;
	private int step = 8;
	private TileEntityHowlerAlarm alarm;

	@SuppressWarnings("resource")
	public GuiHowlerAlarmSlider(int x, int y, TileEntityHowlerAlarm alarm) {
		super(x, y, 107, 16, CommonComponents.EMPTY);
		this.alarm = alarm;
		dragging = false;
		if (alarm.getLevel().isClientSide())
			maxValue = ConfigHandler.MAX_ALARM_RANGE.get();
		int currentRange = alarm.getRange();
		if (alarm.getLevel().isClientSide() && currentRange > maxValue)
			currentRange = maxValue;
		sliderValue = ((float) currentRange - minValue) / (maxValue - minValue);
		setMessage(Component.translatable("msg.ec.HowlerAlarmSoundRange", getNormalizedValue()));
	}

	private int getNormalizedValue() {
		return (minValue + (int) Math.floor((maxValue - minValue) * sliderValue)) / step * step;
	}

	@SuppressWarnings("resource")
	private void setSliderPos(double targetX) {
		sliderValue = (float) (targetX - (getX() + 4)) / (float) (width - 8);
		
		if (sliderValue < 0.0F)
			sliderValue = 0.0F;
		
		if (sliderValue > 1.0F)
			sliderValue = 1.0F;
		
		int newValue = getNormalizedValue();
		if (alarm.getLevel().isClientSide() && alarm.getRange() != newValue) {
			NetworkHelper.updateSeverTileEntity(alarm.getBlockPos(), 2, newValue);
			alarm.setRange(newValue);
		}
		setMessage(Component.translatable("msg.ec.HowlerAlarmSoundRange", newValue));
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;
		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;
		if (dragging)
			setSliderPos(mouseX);

		matrixStack.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX() + (int) (sliderValue * (width - 8)), getY(), 131, 0, 8, 16, 256, 256);
		matrixStack.text(fontRenderer, getMessage(), getX(), getY() - 12, ARGB.opaque(0x404040), false);
	}

	@Override
	public void onPress(InputWithModifiers input) { }

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		double mouseX = event.x();
		setSliderPos(mouseX);
		dragging = true;
	}

	@Override
	public void onRelease(MouseButtonEvent event) {
		dragging = false;
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) { }
}
