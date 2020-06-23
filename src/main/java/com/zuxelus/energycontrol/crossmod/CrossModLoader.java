package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.crossmod.buildcraft.BCCross;
import com.zuxelus.energycontrol.crossmod.buildcraft.BuildCraftCross;
import com.zuxelus.energycontrol.crossmod.computercraft.PeripheralProvider;
import com.zuxelus.energycontrol.crossmod.ic2.IC2Cross;
import com.zuxelus.energycontrol.crossmod.opencomputers.OpenComputersCross;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraftforge.fml.common.Loader;

public class CrossModLoader {
	public static IC2Cross ic2;
	public static BCCross buildCraft;
	public static OpenComputersCross openComputers;
	public static DraconicEvolutionCross draconicEvolution;
	public static CrossAppEng appEng;

	public static void preinit() {
		draconicEvolution = new DraconicEvolutionCross();
		buildCraft = BCCross.getBuildCraftCross();
		ic2 = IC2Cross.getIC2Cross();
		appEng = new CrossAppEng();
	}

	public static void init() {
		openComputers = new OpenComputersCross();
		if (Loader.isModLoaded("computercraft"))
			ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) new PeripheralProvider());
	}

	public static void postinit() { }
}
