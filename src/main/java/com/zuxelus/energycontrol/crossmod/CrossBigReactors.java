package com.zuxelus.energycontrol.crossmod;

import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPartBase;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;

public class CrossBigReactors extends CrossModBase {

	public CrossBigReactors() {
		super("BigReactors", null, null);
	}

	public FluidTankInfo[] getAllTanks(TileEntity te) {
		if (!modLoaded)
			return null;

		if (te instanceof TileEntityReactorPartBase) {
			MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
			if (reactor == null)
				return null;
			if (reactor.isPassivelyCooled())
				return null;
			return reactor.getCoolantContainer().getTankInfo(-1);
		}
		if (te instanceof TileEntityTurbinePartBase) {
			MultiblockTurbine turbine = ((TileEntityTurbinePartBase) te).getTurbine();
			if (turbine == null)
				return null;

			return turbine.getTankInfo();
		}
		return null;
	}

	public int getReactorHeat(World world, int x, int y, int z) {
		TileEntity te;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			te = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if (te instanceof TileEntityReactorPartBase) {
				MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
				if (reactor != null)
					return (int) reactor.getReactorHeat();
			}
		}
		return -1;
	}
}
