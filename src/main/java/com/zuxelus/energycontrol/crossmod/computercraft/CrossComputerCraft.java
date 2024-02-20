package com.zuxelus.energycontrol.crossmod.computercraft;

import com.zuxelus.energycontrol.crossmod.CrossModBase;

import dan200.computercraft.api.ForgeComputerCraftAPI;

public class CrossComputerCraft extends CrossModBase {

	public CrossComputerCraft() {
		ForgeComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
	}
}
