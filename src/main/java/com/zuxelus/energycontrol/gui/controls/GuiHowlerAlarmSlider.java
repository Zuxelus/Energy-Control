package com.zuxelus.energycontrol.gui.controls;

import net.minecraft.text.Text;
import net.minecraft.screen.ScreenTexts;

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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiHowlerAlarmSlider extends PressableWidget {
	private static final Identifier TEXTURE = Identifier.of(EnergyControl.MODID, "textures/gui/gui_howler_alarm.png");

	public float sliderValue;
	public boolean dragging;
	private int minValue = 0;
	private int maxValue = 256;
	private int step = 8;
	private TileEntityHowlerAlarm alarm;

	@SuppressWarnings("resource")
	public GuiHowlerAlarmSlider(int x, int y, TileEntityHowlerAlarm alarm) {
		super(x, y, 107, 16, ScreenTexts.EMPTY);
		this.alarm = alarm;
		dragging = false;
		if (alarm.getWorld().isClient)
			maxValue = ConfigHandler.maxAlarmRange;
		int currentRange = alarm.getRange();
		if (alarm.getWorld().isClient && currentRange > maxValue)
			currentRange = maxValue;
		sliderValue = ((float) currentRange - minValue) / (maxValue - minValue);
		setMessage(Text.translatable("msg.ec.HowlerAlarmSoundRange", getNormalizedValue()));
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
		if (alarm.getWorld().isClient && alarm.getRange() != newValue) {
			NetworkHelper.updateSeverTileEntity(alarm.getPos(), 2, newValue);
			alarm.setRange(newValue);
		}
		setMessage(Text.translatable("msg.ec.HowlerAlarmSoundRange", newValue));
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;
		MinecraftClient minecraft = MinecraftClient.getInstance();
		TextRenderer fontRenderer = minecraft.textRenderer;
		if (dragging)
			setSliderPos(mouseX);

		context.drawTexture(TEXTURE, getX() + (int) (sliderValue * (width - 8)), getY(), 131, 0, 8, 16);
		context.drawText(fontRenderer, getMessage(), getX(), getY() - 12, 0x404040, false);
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
	public void appendClickableNarrations(NarrationMessageBuilder var1) {
		// TODO Auto-generated method stub
	}
}
