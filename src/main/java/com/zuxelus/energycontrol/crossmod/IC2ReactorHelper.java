package com.zuxelus.energycontrol.crossmod;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class IC2ReactorHelper {
	private static final double STEAM_PER_EU = 3.2D;

	public static IReactor getReactorAround(Level world, BlockPos pos) {
		if (world == null)
			return null;

		IReactor reactor = getReactorAt(world, pos);
		if (reactor != null)
			return reactor;

		return getReactorNextBlock(world, pos);
	}

	private static IReactor getReactorNextBlock(Level world, BlockPos pos) {
		if (world == null)
			return null;

		IReactor reactor = null;
		for (Direction dir : Direction.values()) {
			reactor = getReactorAt(world, pos.relative(dir));
			if (reactor != null)
				break;
		}
		return reactor;
	}

	public static IReactor getReactorAt(Level world, BlockPos pos) {
		if (world == null)
			return null;

		BlockEntity entity = world.getBlockEntity(pos);
		if (entity instanceof IReactor)
			return (IReactor) entity;
		if (entity instanceof IReactorChamber)
			return ((IReactorChamber) entity).getReactor();
		return null;
	}

	public static IReactor getReactor3x3(Level world, BlockPos pos) {
		if (world == null)
			return null;
		for (int xoffset = -1; xoffset < 2; xoffset++)
			for (int yoffset = -1; yoffset < 2; yoffset++)
				for (int zoffset = -1; zoffset < 2; zoffset++) {
					BlockEntity te = world.getBlockEntity(pos.east(xoffset).above(yoffset).south(zoffset));
					if (te instanceof IReactor)
						return (IReactor) te;
					if (te instanceof IReactorChamber)
						return ((IReactorChamber) te).getReactor();
				}
		return null;
	}

	public static BlockPos getTargetCoordinates(Level world, BlockPos pos) {
		IReactor reactor = getReactorAt(world, pos);
		if (reactor != null)
			return reactor.getPosition();
		return null;
	}

	public static BlockPos get5x5TargetCoordinates(Level world, BlockPos pos) {
		IReactor reactor = getReactor3x3(world, pos);
		if (reactor != null)
			return reactor.getPosition();
		return null;
	}

	public static int euToSteam(int eu) {
		return (int) Math.floor((eu) * STEAM_PER_EU);
	}
}
