package com.zuxelus.energycontrol.crossmod.computercraft;

import com.zuxelus.energycontrol.crossmod.CrossModBase;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class CrossComputerCraft extends CrossModBase {

	public CrossComputerCraft() {
		ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) new PeripheralProvider());
	}
}
