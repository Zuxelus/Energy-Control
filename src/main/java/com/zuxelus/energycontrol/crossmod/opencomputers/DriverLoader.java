package com.zuxelus.energycontrol.crossmod.opencomputers;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;

import li.cil.oc.api.Driver;

public class DriverLoader {
	public static void registerItems() {
		Driver.add(new DriverAdvancedInfoPanel());
		Driver.add(new DriverAverageCounter());
		Driver.add(new DriverEnergyCounter());
		Driver.add(new DriverHowlerAlarm());
		Driver.add(new DriverInfoPanel());
		Driver.add(new DriverThermalMonitor());
	}
}
