package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.EnergyControl;

import cpw.mods.fml.common.Loader;

public abstract class CrossModBase {
	public boolean modLoaded = false;

	public CrossModBase(String modname, String classname, String message) {
		if (Loader.isModLoaded(modname)) {
			modLoaded = true;
			if (classname != null) {
				try {
					Class.forName(classname, false, this.getClass().getClassLoader());
				} catch (ClassNotFoundException e) {
					modLoaded = false;
				}
			}
			if (modLoaded && message != null)
				EnergyControl.logger.info(message);
		}
	}
}
