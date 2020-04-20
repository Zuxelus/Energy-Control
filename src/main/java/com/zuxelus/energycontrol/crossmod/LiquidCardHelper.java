package com.zuxelus.energycontrol.crossmod;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class LiquidCardHelper {
	public static IFluidTankProperties getStorageAt(World world, BlockPos pos) {
		if (world == null)
			return null;
		
		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof IFluidHandler) {
			IFluidTankProperties[] tanks = FluidTankProperties.convert(((IFluidHandler) entity).getTankInfo(EnumFacing.UP));
			if (tanks == null || tanks.length == 0)
				return null;
			return tanks[0];
		}
		return null;
	}
}
