package com.zuxelus.energycontrol.hooks;

import com.zuxelus.hooklib.minecraft.HookLoader;
import com.zuxelus.hooklib.minecraft.PrimaryClassTransformer;

public class ECHookLoader extends HookLoader {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { PrimaryClassTransformer.class.getName() };
	}

	@Override
	public void registerHooks() {
		registerHookContainer("com.zuxelus.energycontrol.hooks.DraconicEvolutionHooks");
		registerHookContainer("com.zuxelus.energycontrol.hooks.GalacticraftHooks");
		registerHookContainer("com.zuxelus.energycontrol.hooks.HBMHooks");
		registerHookContainer("com.zuxelus.energycontrol.hooks.IC2Hooks");
		registerHookContainer("com.zuxelus.energycontrol.hooks.IC2ClassicHooks");
		registerHookContainer("com.zuxelus.energycontrol.hooks.MekanismHooks");
		registerHookContainer("com.zuxelus.energycontrol.hooks.ThermalExpansionHooks");
	}
}
