package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.crossmod.ic2.IC2Cross;
import com.zuxelus.energycontrol.crossmod.opencomputers.OpenComputersCross;

public class CrossModLoader {
	public static IC2Cross ic2;
	public static OpenComputersCross openComputers;
	public static DraconicEvolutionCross draconicEvolution;

	public static void preinit() {
		draconicEvolution = new DraconicEvolutionCross();
		ic2 = IC2Cross.getIC2Cross();
	}

	public static void init() {
		openComputers = new OpenComputersCross();
	}

	public static void postinit() { }
}
