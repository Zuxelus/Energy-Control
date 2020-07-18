package com.zuxelus.energycontrol.crossmod;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class LiquidCardHelper {
	public static IFluidTankProperties[] getAllTanks(World world, BlockPos pos) {
		if (world == null)
			return null;

		TileEntity te = world.getTileEntity(pos);
		if (te == null)
			return null;

		if (te instanceof IFluidHandler)
			return ((IFluidHandler) te).getTankProperties();

		IFluidTankProperties[] list = CrossModLoader.ic2.getAllTanks(te);
		if (list != null)
			return list;
		list = CrossModLoader.techReborn.getAllTanks(te);
		if (list != null)
			return list;
		list = CrossModLoader.galacticraft.getAllTanks(te);
		if (list != null)
			return list;
		return CrossModLoader.bigReactors.getAllTanks(te);
	}

	public static IFluidTankProperties getStorageAt(World world, BlockPos pos) {
		IFluidTankProperties[] tanks = getAllTanks(world, pos);
		if (tanks == null || tanks.length == 0)
			return null;
		return tanks[0];
	}
}
