package com.zuxelus.energycontrol.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmListBox;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmSlider;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.gui.GuiBase;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHowlerAlarm extends GuiBase {
	private TileEntityHowlerAlarm alarm;
	private GuiHowlerAlarmSlider slider;
	private GuiHowlerAlarmListBox listBox;
	private boolean isBig;

	public GuiHowlerAlarm(TileEntityHowlerAlarm alarm, boolean isBig) {
		super("tile.howler_alarm.name", 131, isBig ? 236 : 136, isBig ? 
			EnergyControl.MODID + ":textures/gui/gui_howler_alarm_big.png" :
			EnergyControl.MODID + ":textures/gui/gui_howler_alarm.png");
		this.alarm = alarm;
		this.isBig = isBig;
	}

	@Override
	public void initGui() {
		super.initGui();
		slider = new GuiHowlerAlarmSlider(3, guiLeft + 12, guiTop + 33, alarm);

		List<String> items = new ArrayList<String>(EnergyControl.instance.availableAlarms);
		items.retainAll(EnergyControl.instance.serverAllowedAlarms);

		listBox = new GuiHowlerAlarmListBox(4, guiLeft + 13, guiTop + 63, 105, isBig? 165 : 65, items, alarm);
		addButton(slider);
		addButton(listBox);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
		fontRenderer.drawString(I18n.format("msg.ec.HowlerAlarmSound"), 12, 53, 0x404040);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int which) {
		super.mouseReleased(mouseX, mouseY, which);
		if ((which == 0 || which == 1) && (slider.dragging || listBox.dragging)) {
			slider.mouseReleased(mouseX, mouseY);
			listBox.mouseReleased(mouseX, mouseY);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		listBox.keyPressed(typedChar, keyCode);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int wheel = Mouse.getEventDWheel();
		if (wheel != 0) {
			int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
			int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
			listBox.mouseScrolled(i, j, wheel);
		}
	}
}
