package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.crossmod.bigreactors.CrossBigReactors;
import com.zuxelus.energycontrol.crossmod.buildcraft.CrossBC;
import com.zuxelus.energycontrol.crossmod.computercraft.PeripheralProvider;
import com.zuxelus.energycontrol.crossmod.ic2.CrossIC2;
import com.zuxelus.energycontrol.crossmod.opencomputers.CrossOpenComputers;
import com.zuxelus.energycontrol.crossmod.techreborn.CrossTechReborn;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraftforge.fml.common.Loader;

public class CrossModLoader {
	public static CrossIC2 ic2;
	public static CrossTechReborn techReborn;
	public static CrossBC buildCraft;
	public static CrossOpenComputers openComputers;
	public static CrossAppEng appEng;
	public static CrossGalacticraft galacticraft;
	public static CrossBigReactors bigReactors;

	public static void preinit() {
		ic2 = CrossIC2.getMod();
		techReborn = CrossTechReborn.getMod();
		buildCraft = CrossBC.getMod();
		appEng = new CrossAppEng();
		galacticraft = new CrossGalacticraft();
		bigReactors = CrossBigReactors.getMod();
	}

	public static void init() {
		openComputers = new CrossOpenComputers();
		if (Loader.isModLoaded("computercraft"))
			ComputerCraftAPI.registerPeripheralProvider((IPeripheralProvider) new PeripheralProvider());
	}

	public static void postinit() { }
}
