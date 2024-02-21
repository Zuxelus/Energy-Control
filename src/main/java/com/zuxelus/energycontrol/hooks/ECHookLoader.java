package com.zuxelus.energycontrol.hooks;

import com.zuxelus.hooklib.minecraft.HookLoader;
import com.zuxelus.hooklib.minecraft.PrimaryClassTransformer;

import net.minecraft.tileentity.TileEntity;

public class ECHookLoader extends HookLoader {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { PrimaryClassTransformer.class.getName() };
	}

	@Override
	public void registerHooks() {
		registerHookContainer("com.zuxelus.energycontrol.hooks.BaseHooks");
		registerHookContainer("com.zuxelus.energycontrol.hooks.DraconicEvolutionHooks");
		registerHookContainer("com.zuxelus.energycontrol.hooks.EnderIOHooks");
		registerHookContainer("com.zuxelus.energycontrol.hooks.GregTechHooks");
		registerHookContainer("com.zuxelus.energycontrol.hooks.HBMHooks");
		registerHookContainer("com.zuxelus.energycontrol.hooks.IC2Hooks");
		registerHookContainer("com.zuxelus.energycontrol.hooks.NuclearCraftHooks");
	}

	public static void removeTileEntity(TileEntity te) {
		if (te == null)
			return;
		GregTechHooks.map.remove(te);
	}
}
