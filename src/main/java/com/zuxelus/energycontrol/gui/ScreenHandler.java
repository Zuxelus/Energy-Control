package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.client.Minecraft;

public class ScreenHandler {

	public static void openHowlerAlarmScreen(TileEntityHowlerAlarm be) {
		Minecraft.getInstance().setScreen(new GuiHowlerAlarm(be));
	}

	public static void openIndustrialAlarmScreen(TileEntityIndustrialAlarm be) {
		Minecraft.getInstance().setScreen(new GuiIndustrialAlarm(be));
	}

	public static void openThermalMonitorScreen(TileEntityThermalMonitor be) {
		Minecraft.getInstance().setScreen(new GuiThermalMonitor(be));
	}
}
