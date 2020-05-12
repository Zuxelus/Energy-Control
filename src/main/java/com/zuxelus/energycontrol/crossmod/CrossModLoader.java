package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.crossmod.ic2.IC2Cross;
import com.zuxelus.energycontrol.crossmod.opencomputers.OpenComputersCross;

public class CrossModLoader {
	public static IC2Cross crossIc2;
	public static OpenComputersCross openComputers;
	public static DraconicEvolutionCross draconicEvolution;
	//public static CrossRailcraft crossRailcraft;

	public static void preinit() {
		draconicEvolution = new DraconicEvolutionCross();
	}

	public static void init() {
		crossIc2 = IC2Cross.getIC2Cross();
		//crossRailcraft = new CrossRailcraft();
		openComputers = new OpenComputersCross();
	}

	public static void postinit() { }
}
