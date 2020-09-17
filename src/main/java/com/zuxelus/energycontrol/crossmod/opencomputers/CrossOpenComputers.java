package com.zuxelus.energycontrol.crossmod.opencomputers;

import com.zuxelus.energycontrol.crossmod.CrossModBase;

public class CrossOpenComputers extends CrossModBase {

	public CrossOpenComputers() {
		super("OpenComputers", "li.cil.oc.api.Driver", null);
		if (modLoaded)
			DriverLoader.registerItems();
	}
}
