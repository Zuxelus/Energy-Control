package com.zuxelus.energycontrol.gui;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmListBox;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmSlider;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiHowlerAlarm extends GuiBase {
	private TileEntityHowlerAlarm alarm;
	private GuiHowlerAlarmSlider slider;
	private GuiHowlerAlarmListBox listBox;

	public GuiHowlerAlarm(TileEntityHowlerAlarm alarm) {
		super("tile.howler_alarm.name", 131, 136, EnergyControl.MODID + ":textures/gui/gui_howler_alarm.png");
		this.alarm = alarm;
	}

	@Override
	public void init() {
		super.init();
		slider = new GuiHowlerAlarmSlider(guiLeft + 12, guiTop + 33, alarm);

		List<String> items = new ArrayList<String>(EnergyControl.INSTANCE.availableAlarms);
		items.retainAll(EnergyControl.INSTANCE.serverAllowedAlarms);

		listBox = new GuiHowlerAlarmListBox(guiLeft + 13, guiTop + 63, 105, 65, items, alarm);
		addButton(slider);
		addButton(listBox);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		font.drawString(title.getFormattedText(), (xSize - font.getStringWidth(title.getFormattedText())) / 2, 6, 0x404040);
		font.drawString(I18n.format("msg.ec.HowlerAlarmSound"), 12, 53, 0x404040);
	}
}
