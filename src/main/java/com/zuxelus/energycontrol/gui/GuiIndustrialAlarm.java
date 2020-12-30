package com.zuxelus.energycontrol.gui;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.controls.GuiHowlerAlarmSlider;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.gui.GuiBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

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
		guiLeft = (this.width - xSize) / 2;
		guiTop = (this.height - ySize) / 2;
		buttonList.clear();
		slider = new GuiHowlerAlarmSlider(3, guiLeft + 12, guiTop + 33, alarm);
		buttonList.add(slider);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(name, (xSize - fontRendererObj.getStringWidth(name)) / 2, 6, 0x404040);
	}

	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int which) {
		super.mouseMovedOrUp(mouseX, mouseY, which);
		if ((which == 0 || which == 1) && slider.dragging)
			slider.mouseReleased(mouseX, mouseY);
	}
}
