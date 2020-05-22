package com.zuxelus.energycontrol.utils;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReactorHelper {
	private static final double STEAM_PER_EU = 3.2D;
	
	public static IReactor getReactorAround(World world, BlockPos pos) {
		if (world == null)
			return null;
		
		IReactor reactor = getReactorAt(world, pos);
		if (reactor != null)
			return reactor;
		
		return getReactorNextBlock(world, pos);
	}	
	
	public static IReactor getReactorNextBlock(World world, BlockPos pos) {
		if (world == null)
			return null;
		
		IReactor reactor = null;
		for (EnumFacing dir : EnumFacing.VALUES) {
			reactor = getReactorAt(world, pos.offset(dir));
			if (reactor != null)
				break;
		}
		return reactor;
	}
	
	public static IReactor getReactorAt(World world, BlockPos pos) {
		if (world == null)
			return null;
		
		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof IReactor)
			return (IReactor) entity;
		if (entity instanceof IReactorChamber)
			return ((IReactorChamber) entity).getReactorInstance();
		return null;
	}
	
	public static IReactor getReactor3x3(World world, BlockPos pos) {
		if (world == null)
			return null;
		for (int xoffset = -1; xoffset < 2; xoffset++) {
			for (int yoffset = -1; yoffset < 2; yoffset++) {
				for (int zoffset = -1; zoffset < 2; zoffset++) {
					TileEntity te = world.getTileEntity(pos.east(xoffset).up(yoffset).south(zoffset));
					if (te instanceof IReactor)
						return (IReactor) te;
					if (te instanceof IReactorChamber)
						return ((IReactorChamber) te).getReactorInstance();
				}
			}
		}
		return null;
	}

	public static BlockPos getTargetCoordinates(World world, BlockPos pos) {
		IReactor reactor = ReactorHelper.getReactorAt(world, pos);
		if (reactor != null)
			return reactor.getPosition();
		return null;
	}

	public static BlockPos get5x5TargetCoordinates(World world, BlockPos pos) {
		IReactor reactor = ReactorHelper.getReactor3x3(world, pos);
		if (reactor != null)
			return reactor.getPosition();
		return null;
	}

	public static int euToSteam(int eu) {
		return (int) Math.floor((eu) * STEAM_PER_EU);
	}

	public static boolean isSteam(IReactor reactor) {
		return CrossModLoader.ic2.isSteamReactor((TileEntity) reactor);
	}

	public static int getNuclearCellTimeLeft(ItemStack rStack) {
		return CrossModLoader.ic2.getNuclearCellTimeLeft(rStack);
	}
}
