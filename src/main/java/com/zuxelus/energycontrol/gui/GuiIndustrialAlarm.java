package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmSlider;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.gui.GuiBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@Environment(EnvType.CLIENT)
public class GuiIndustrialAlarm extends GuiBase {
	private TileEntityHowlerAlarm alarm;
	private GuiHowlerAlarmSlider slider;

	public GuiIndustrialAlarm(TileEntityHowlerAlarm alarm) {
		super("block.energycontrol.industrial_alarm", 131, 64, EnergyControl.MODID + ":textures/gui/gui_industrial_alarm.png");
		this.alarm = alarm;
	}

	@Override
	public void init() {
		super.init();
		slider = new GuiHowlerAlarmSlider(guiLeft + 12, guiTop + 33, alarm);
		addRenderableWidget(slider);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(GuiGraphicsExtractor context, int mouseX, int mouseY) {
		drawTitle(context);
	}
}
