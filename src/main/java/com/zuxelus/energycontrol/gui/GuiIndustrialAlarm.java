package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmSlider;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.gui.GuiBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIndustrialAlarm extends GuiBase {
	private TileEntityHowlerAlarm alarm;
	private GuiHowlerAlarmSlider slider;

	public GuiIndustrialAlarm(TileEntityHowlerAlarm alarm) {
		super("tile.industrial_alarm.name", 131, 64, EnergyControl.MODID + ":textures/gui/gui_industrial_alarm.png");
		this.alarm = alarm;
	}

	@Override
	public void initGui() {
		super.initGui();
		slider = new GuiHowlerAlarmSlider(3, guiLeft + 12, guiTop + 33, alarm);
		addButton(slider);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(name, (xSize - fontRendererObj.getStringWidth(name)) / 2, 6, 0x404040);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int which) {
		super.mouseReleased(mouseX, mouseY, which);
		if ((which == 0 || which == 1) && slider.dragging)
			slider.mouseReleased(mouseX, mouseY);
	}
}
