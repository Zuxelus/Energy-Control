package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class LiquidCardHelper {
	public static List<IFluidTank> getAllTanks(World world, BlockPos pos) {
		if (world == null)
			return null;

		TileEntity te = world.getTileEntity(pos);
		if (te == null)
			return null;

		if (te instanceof IFluidHandler) {
			IFluidTankProperties[] tanks = ((IFluidHandler) te).getTankProperties();
			List<IFluidTank> result = new ArrayList<>();
			for (IFluidTankProperties tank : tanks)
				result.add(new FluidTank(tank.getContents(), tank.getCapacity()));
			return result;
		}

		List<IFluidTank> list = CrossModLoader.ic2.getAllTanks(te);
		if (list != null)
			return list;
		list = CrossModLoader.techReborn.getAllTanks(te);
		if (list != null)
			return list;
		list = CrossModLoader.galacticraft.getAllTanks(te);
		if (list != null)
			return list;
		list = CrossModLoader.bigReactors.getAllTanks(te);
		if (list != null)
			return list;
		return CrossModLoader.buildCraft.getAllTanks(te);
	}

	public static IFluidTank getStorageAt(World world, BlockPos pos) {
		List<IFluidTank> tanks = getAllTanks(world, pos);
		if (tanks == null || tanks.size() == 0)
			return null;
		return tanks.iterator().next();
	}
}
