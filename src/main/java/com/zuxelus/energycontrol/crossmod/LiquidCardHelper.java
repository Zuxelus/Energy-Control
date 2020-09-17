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

		FluidTankInfo[] list = CrossModLoader.galacticraft.getAllTanks(te);
		if (list != null)
			return list;

		if (te instanceof IFluidHandler)
			return ((IFluidHandler) te).getTankInfo(null);

		list = CrossModLoader.ic2.getAllTanks(te);
		if (list != null)
			return list;
		list = CrossModLoader.techReborn.getAllTanks(te);
		if (list != null)
			return list;
		return CrossModLoader.bigReactors.getAllTanks(te);
	}

	public static FluidTankInfo getStorageAt(World world, int x, int y, int z) {
		FluidTankInfo[] tanks = getAllTanks(world, x, y, z);
		if (tanks == null || tanks.length == 0)
			return null;
		return tanks[0];
	}
}
