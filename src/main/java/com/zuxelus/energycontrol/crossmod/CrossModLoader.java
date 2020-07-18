package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.crossmod.ic2.CrossIC2;
import com.zuxelus.energycontrol.crossmod.opencomputers.CrossOpenComputers;
import com.zuxelus.energycontrol.crossmod.techreborn.CrossTechReborn;

import net.minecraftforge.fml.common.Loader;

public class CrossModLoader {
	public static CrossIC2 ic2;
	public static CrossTechReborn techReborn;
	public static CrossOpenComputers openComputers;
	public static CrossAppEng appEng;
	public static CrossGalacticraft galacticraft;
	public static CrossBigReactors bigReactors;

	public static void preinit() {
		ic2 = CrossIC2.getMod();
		techReborn = CrossTechReborn.getMod();
		appEng = new CrossAppEng();
		galacticraft = new CrossGalacticraft();
		bigReactors = new CrossBigReactors();
	}

	public static void init() {
		openComputers = new CrossOpenComputers();
	}

	public static void postinit() { }
}
