package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.client.MinecraftClient;

public class ScreenHandler {

	public static void openHowlerAlarmScreen(TileEntityHowlerAlarm be) {
		MinecraftClient.getInstance().setScreen(new GuiHowlerAlarm(be));
	}

	public static void openIndustrialAlarmScreen(TileEntityIndustrialAlarm be) {
		MinecraftClient.getInstance().setScreen(new GuiIndustrialAlarm(be));
	}

	public static void openThermalMonitorScreen(TileEntityThermalMonitor be) {
		MinecraftClient.getInstance().setScreen(new GuiThermalMonitor(be));
	}
}
