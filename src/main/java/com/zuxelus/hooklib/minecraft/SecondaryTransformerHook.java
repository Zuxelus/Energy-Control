package com.zuxelus.hooklib.minecraft;

import com.zuxelus.hooklib.asm.Hook;

import cpw.mods.fml.common.Loader;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class SecondaryTransformerHook {

	@Hook
	public static void injectData(Loader loader, Object... data) {
		ClassLoader classLoader = SecondaryTransformerHook.class.getClassLoader();
		if (classLoader instanceof LaunchClassLoader) {
			((LaunchClassLoader) classLoader).registerTransformer(MinecraftClassTransformer.class.getName());
		} else {
			System.out.println("HookLib was not loaded by LaunchClassLoader. Hooks will not be injected.");
		}
	}
}
