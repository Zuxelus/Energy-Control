package com.zuxelus.energycontrol.crossmod.opencomputers;

import com.zuxelus.energycontrol.crossmod.CrossModBase;

public class OpenComputersCross extends CrossModBase {

	public OpenComputersCross() {
		super("opencomputers", "li.cil.oc.api.Driver", null);
		if (modLoaded)
			DriverLoader.registerItems();
	}
}
