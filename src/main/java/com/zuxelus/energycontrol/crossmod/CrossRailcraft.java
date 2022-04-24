package com.zuxelus.energycontrol.crossmod;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;

import java.lang.reflect.Method;

public class CrossRailcraft {
	public boolean modLoaded;
	private Class<?> _tankTile;

	public CrossRailcraft() {
		try {
			_tankTile = Class.forName("mods.railcraft.common.blocks.machine.ITankTile", false, this.getClass().getClassLoader());
			modLoaded = true;
		} catch (ClassNotFoundException ignored) { }
	}

	public FluidTankInfo getIronTank(TileEntity entity) {
		if (!modLoaded || entity == null)
			return null;
		
		try {
			if (_tankTile.isAssignableFrom(entity.getClass())) {
				Method method = entity.getClass().getMethod("getTank");
				FluidTank tank = (FluidTank) method.invoke(entity);
				if (tank != null)
					return tank.getInfo();
			}
		} catch (Exception ignored) {	}
		return null;
	}
}
