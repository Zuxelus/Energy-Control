package com.zuxelus.energycontrol.gui;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmListBox;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmSlider;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.gui.GuiBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiHowlerAlarm extends GuiBase {
	private TileEntityHowlerAlarm alarm;
	private GuiHowlerAlarmSlider slider;
	private GuiHowlerAlarmListBox listBox;

	public GuiHowlerAlarm(TileEntityHowlerAlarm alarm) {
		super("tile.howler_alarm.name", 131, 136, EnergyControl.MODID + ":textures/gui/gui_howler_alarm.png");
		this.alarm = alarm;
	}

	@Override
	public void initGui() {
		super.initGui();
		slider = new GuiHowlerAlarmSlider(3, guiLeft + 12, guiTop + 33, alarm);

		List<String> items = new ArrayList<>(EnergyControl.instance.availableAlarms);
		items.retainAll(EnergyControl.instance.serverAllowedAlarms);

		listBox = new GuiHowlerAlarmListBox(4, guiLeft + 13, guiTop + 63, 105, 65, items, alarm);
		buttonList.add(slider);
		buttonList.add(listBox);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(name, (xSize - fontRendererObj.getStringWidth(name)) / 2, 6, 0x404040);
		fontRendererObj.drawString(I18n.format("msg.ec.HowlerAlarmSound"), 12, 53, 0x404040);
	}

	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int which) {
		super.mouseMovedOrUp(mouseX, mouseY, which);
		if ((which == 0 || which == 1) && (slider.dragging || listBox.dragging)) {
			slider.mouseReleased(mouseX, mouseY);
			listBox.mouseReleased(mouseX, mouseY);
		}
	}
}
