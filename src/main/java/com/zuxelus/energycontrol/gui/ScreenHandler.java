package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.client.MinecraftClient;

public class ScreenHandler {

	public static void openHowlerAlarmScreen(TileEntityHowlerAlarm be) {
		MinecraftClient.getInstance().openScreen(new GuiHowlerAlarm(be));
	}

	public static void openIndustrialAlarmScreen(TileEntityIndustrialAlarm be) {
		MinecraftClient.getInstance().openScreen(new GuiIndustrialAlarm(be));
	}

	public static void openThermalMonitorScreen(TileEntityThermalMonitor be) {
		MinecraftClient.getInstance().openScreen(new GuiThermalMonitor(be));
	}
}
