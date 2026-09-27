package com.zuxelus.energycontrol.gui;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.client.Minecraft;

public class ScreenHandler {

	// alarms present on the client and allowed by the server
	public static List<String> getAlarms() {
		List<String> items = EnergyControl.INSTANCE.availableAlarms == null ? new ArrayList<>() : new ArrayList<>(EnergyControl.INSTANCE.availableAlarms);
		if (EnergyControl.INSTANCE.serverAllowedAlarms != null)
			items.retainAll(EnergyControl.INSTANCE.serverAllowedAlarms);
		return items;
	}

	public static void openHowlerAlarmScreen(TileEntityHowlerAlarm be) {
		List<String> items = getAlarms();
		Minecraft.getInstance().gui.setScreen(new GuiHowlerAlarm(be, items.size() > 10));
	}

	public static void openIndustrialAlarmScreen(TileEntityIndustrialAlarm be) {
		Minecraft.getInstance().gui.setScreen(new GuiIndustrialAlarm(be));
	}

	public static void openThermalMonitorScreen(TileEntityThermalMonitor be) {
		Minecraft.getInstance().gui.setScreen(new GuiThermalMonitor(be));
	}
}
