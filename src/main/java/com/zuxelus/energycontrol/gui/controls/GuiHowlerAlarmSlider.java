package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiHowlerAlarmSlider extends AbstractButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_howler_alarm.png");

	public float sliderValue;
	public boolean dragging;
	private int minValue = 0;
	private int maxValue = 256;
	private int step = 8;
	private TileEntityHowlerAlarm alarm;

	@SuppressWarnings("resource")
	public GuiHowlerAlarmSlider(int x, int y, TileEntityHowlerAlarm alarm) {
		super(x, y, 107, 16, StringTextComponent.EMPTY);
		this.alarm = alarm;
		dragging = false;
		if (alarm.getWorld().isRemote)
			maxValue = ConfigHandler.MAX_ALARM_RANGE.get();
		int currentRange = alarm.getRange();
		if (alarm.getWorld().isRemote && currentRange > maxValue)
			currentRange = maxValue;
		sliderValue = ((float) currentRange - minValue) / (maxValue - minValue);
		setMessage(new TranslationTextComponent("msg.ec.HowlerAlarmSoundRange", getNormalizedValue()));
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
		if (alarm.getWorld().isRemote && alarm.getRange() != newValue) {
			NetworkHelper.updateSeverTileEntity(alarm.getPos(), 2, newValue);
			alarm.setRange(newValue);
		}
		setMessage(new TranslationTextComponent("msg.ec.HowlerAlarmSoundRange", newValue));
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontRenderer = minecraft.fontRenderer;
		minecraft.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (dragging)
			setSliderPos(mouseX);
		
		blit(matrixStack, x + (int) (sliderValue * (width - 8)), y, 131, 0, 8, 16);
		fontRenderer.func_243248_b(matrixStack, getMessage(), x, y - 12, 0x404040);
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
}
