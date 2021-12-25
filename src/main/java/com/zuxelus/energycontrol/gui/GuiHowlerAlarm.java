package com.zuxelus.energycontrol.gui;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmListBox;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmSlider;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.gui.GuiBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class GuiHowlerAlarm extends GuiBase {
	private TileEntityHowlerAlarm alarm;
	private GuiHowlerAlarmSlider slider;
	private GuiHowlerAlarmListBox listBox;

	public GuiHowlerAlarm(TileEntityHowlerAlarm alarm) {
		super("block.energycontrol.howler_alarm", 131, 136, EnergyControl.MODID + ":textures/gui/gui_howler_alarm.png");
		this.alarm = alarm;
	}

	@Override
	public void init() {
		super.init();
		slider = new GuiHowlerAlarmSlider(guiLeft + 12, guiTop + 33, alarm);

		List<String> items = new ArrayList<String>(EnergyControl.INSTANCE.availableAlarms);
		items.retainAll(EnergyControl.INSTANCE.serverAllowedAlarms);

		listBox = new GuiHowlerAlarmListBox(guiLeft + 13, guiTop + 63, 105, 65, items, alarm);
		addDrawableChild(slider);
		addDrawableChild(listBox);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawTitle(matrixStack);
		textRenderer.draw(matrixStack, new TranslatableText("msg.ec.HowlerAlarmSound"), 12, 53, 0x404040);
	}
}
