package com.zuxelus.energycontrol.crossmod;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.registry.Registry;

public class LiquidCardHelper {
	/*public static List<IFluidTank> getAllTanks(World world, BlockPos pos) {
		if (world == null)
			return null;

		BlockEntity be = world.getBlockEntity(pos);
		if (be == null)
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
	}*/

	public static String getFluidName(Fluid fluid) {
		return StringUtils.capitalize(Registry.FLUID.getId(fluid).getPath()).replace("_", " ");
	}
}
