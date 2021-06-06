package com.zuxelus.energycontrol.gui.controls;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHowlerAlarmSlider extends GuiButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_howler_alarm.png");

	public float sliderValue;
	public boolean dragging;
	private int minValue;
	private int maxValue = 256;
	private int step = 8;
	private TileEntityHowlerAlarm alarm;

	public GuiHowlerAlarmSlider(int id, int x, int y, TileEntityHowlerAlarm alarm) {
		super(id, x, y, 107, 16, "");
		this.alarm = alarm;
		dragging = false;
		if (alarm.getWorld().isRemote)
			maxValue = EnergyControl.config.maxAlarmRange;
		int currentRange = alarm.getRange();
		if (alarm.getWorld().isRemote && currentRange > maxValue)
			currentRange = maxValue;
		sliderValue = ((float) currentRange - minValue) / (maxValue - minValue);
		displayString = I18n.format("msg.ec.HowlerAlarmSoundRange", getNormalizedValue());
	}

	private int getNormalizedValue() {
		return (minValue + (int) Math.floor((maxValue - minValue) * sliderValue)) / step * step;
	}

	private void setSliderPos(int targetX) {
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
		displayString = I18n.format("msg.ec.HowlerAlarmSoundRange", newValue);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;
		mc.getTextureManager().bindTexture(TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if (dragging)
			setSliderPos(mouseX);
		
		drawTexturedModalRect(x + (int) (sliderValue * (width - 8)), y, 131, 0, 8, 16);
		mc.fontRenderer.drawString(displayString, x, y - 12, 0x404040);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (!super.mousePressed(mc, mouseX, mouseY))
			return false;
		setSliderPos(mouseX);
		dragging = true;
		return true;
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		super.mouseReleased(mouseX, mouseY);
		dragging = false;
	}
}
