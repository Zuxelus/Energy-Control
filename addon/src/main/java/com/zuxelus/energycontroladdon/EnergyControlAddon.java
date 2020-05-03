package com.zuxelus.energycontroladdon;

import com.zuxelus.energycontrol.api.EnergyContolRegister;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = EnergyControlAddon.MODID, name = EnergyControlAddon.NAME, version = EnergyControlAddon.VERSION, dependencies = "required-after:energycontrol")
public class EnergyControlAddon {
	public static final String MODID = "energycontroladdon";
	public static final String NAME = "Energy Control Addon";
	public static final String VERSION = "@VERSION@";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		EnergyContolRegister.registerKit(new KitFurnace());
		EnergyContolRegister.registerCard(new CardFurnace());
	}
}
