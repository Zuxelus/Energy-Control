package com.zuxelus.energycontrol.crossmod;

import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Fluids;
import ic2.core.block.comp.Fluids.InternalFluidTank;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class LiquidCardHelper {
	public static Iterable<InternalFluidTank> getAllTanks(World world, BlockPos pos) {
		if (world == null)
			return null;
		
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityBlock))
			return null;
		
		if (!((TileEntityBlock)te).hasComponent(Fluids.class))
			return null;

		Fluids fluid = ((TileEntityBlock)te).getComponent(Fluids.class);

		return fluid.getAllTanks();
	}

	public static IFluidTank getStorageAt(World world, BlockPos pos) {
		Iterable<InternalFluidTank> tanks = getAllTanks(world, pos);
		if (tanks == null)
			return null;

		return tanks.iterator().next();
	}
/*	
	public static Iterable<InternalFluidTank> getReactorTanks(TileEntityNuclearReactorElectric te) {		
		if (!(te.hasComponent(Fluids.class)))
			return null;
		Fluids fluid = te.getComponent(Fluids.class);
		return fluid.getAllTanks();
	}*/
}
