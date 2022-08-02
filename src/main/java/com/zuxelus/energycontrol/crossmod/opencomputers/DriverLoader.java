package com.zuxelus.energycontrol.crossmod.opencomputers;

import com.zuxelus.energycontrol.crossmod.ModIDs;

import li.cil.oc.api.Driver;
import net.minecraftforge.fml.common.Loader;

public class DriverLoader {

	public static void registerItems() {
		Driver.add(new DriverAdvancedInfoPanel());
		Driver.add(new DriverAverageCounter());
		Driver.add(new DriverEnergyCounter());
		Driver.add(new DriverHowlerAlarm());
		Driver.add(new DriverInfoPanel());
		Driver.add(new DriverThermalMonitor());
		if (Loader.isModLoaded(ModIDs.IC2))
			Driver.add(new DriverAFSU());
	}
}
