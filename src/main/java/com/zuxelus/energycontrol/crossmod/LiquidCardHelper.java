package com.zuxelus.energycontrol.crossmod;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class LiquidCardHelper {
	public static FluidTankInfo[] getAllTanks(World world, int x, int y, int z) {
		if (world == null)
			return null;

		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null)
			return null;

		if (te instanceof IFluidHandler)
			return ((IFluidHandler) te).getTankInfo(null);

		FluidTankInfo[] list = CrossModLoader.ic2.getAllTanks(te);
		if (list != null)
			return list;
		return null;
	}

	public static FluidTankInfo getStorageAt(World world, int x, int y, int z) {
		FluidTankInfo[] tanks = getAllTanks(world, x, y, z);
		if (tanks == null || tanks.length == 0)
			return null;
		return tanks[0];
	}
}
