package com.zuxelus.energycontrol.gui;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.client.MinecraftClient;

public class ScreenHandler {

	public static void openHowlerAlarmScreen(TileEntityHowlerAlarm be) {
		List<String> items = new ArrayList<String>(EnergyControl.INSTANCE.availableAlarms);
		items.retainAll(EnergyControl.INSTANCE.serverAllowedAlarms);
		MinecraftClient.getInstance().setScreen(new GuiHowlerAlarm(be, items.size() > 10));
	}

	public static void openIndustrialAlarmScreen(TileEntityIndustrialAlarm be) {
		MinecraftClient.getInstance().setScreen(new GuiIndustrialAlarm(be));
	}

	public static void openThermalMonitorScreen(TileEntityThermalMonitor be) {
		MinecraftClient.getInstance().setScreen(new GuiThermalMonitor(be));
	}
}
