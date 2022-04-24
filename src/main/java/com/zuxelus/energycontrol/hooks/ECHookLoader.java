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
		//registerHookContainer("com.zuxelus.energycontrol.hooks.HBMHooks");
	}
}
