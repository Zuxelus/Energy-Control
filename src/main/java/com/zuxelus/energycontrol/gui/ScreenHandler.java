package com.zuxelus.energycontrol.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.*;

public class ScreenHandler {

	public static void openHowlerAlarmScreen(TileEntityHowlerAlarm be) {
		List<String> items = new ArrayList<String>(EnergyControl.INSTANCE.availableAlarms);
		items.retainAll(EnergyControl.INSTANCE.serverAllowedAlarms);
		Minecraft.getInstance().gui.setScreen(new GuiHowlerAlarm(be, items.size() > 10));
	}

	public static void openIndustrialAlarmScreen(TileEntityIndustrialAlarm be) {
		Minecraft.getInstance().gui.setScreen(new GuiIndustrialAlarm(be));
	}

	public static void openThermalMonitorScreen(TileEntityThermalMonitor be) {
		Minecraft.getInstance().gui.setScreen(new GuiThermalMonitor(be));
	}
}
