package com.zuxelus.energycontrol.crossmod;

import ic2.api.item.ICustomDamageItem;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import ic2.core.item.reactor.ItemReactorLithiumCell;
import ic2.core.item.reactor.ItemReactorMOX;
import ic2.core.item.reactor.ItemReactorUranium;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class IC2ReactorHelper {
	private static final double STEAM_PER_EU = 3.2D;

	public static IReactor getReactorAround(World world, int x, int y, int z) {
		if (world == null)
			return null;

		IReactor reactor = getReactorAt(world, x, y, z);
		if (reactor != null)
			return reactor;

		return getReactorNextBlock(world, x, y, z);
	}

	private static IReactor getReactorNextBlock(World world, int x, int y, int z) {
		if (world == null)
			return null;

		IReactor reactor = null;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			reactor = getReactorAt(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if (reactor != null)
				break;
		}
		return reactor;
	}

	public static IReactor getReactorAt(World world, int x, int y, int z) {
		if (world == null)
			return null;

		TileEntity entity = world.getTileEntity(x, y, z);
		if (entity instanceof IReactor)
			return (IReactor) entity;
		if (entity instanceof IReactorChamber)
			return ((IReactorChamber) entity).getReactor();
		return null;
	}

	public static IReactor getReactor3x3(World world, int x, int y, int z) {
		if (world == null)
			return null;
		for (int xoffset = -1; xoffset < 2; xoffset++)
			for (int yoffset = -1; yoffset < 2; yoffset++)
				for (int zoffset = -1; zoffset < 2; zoffset++) {
					TileEntity te = world.getTileEntity(x + xoffset, y + yoffset, z + zoffset);
					if (te instanceof IReactor)
						return (IReactor) te;
					if (te instanceof IReactorChamber)
						return ((IReactorChamber) te).getReactor();
				}
		return null;
	}

	public static ChunkCoordinates getTargetCoordinates(World world, int x, int y, int z) {
		IReactor reactor = getReactorAt(world, x, y, z);
		if (reactor != null)
			return reactor.getPosition();
		return null;
	}

	public static ChunkCoordinates get5x5TargetCoordinates(World world, int x, int y, int z) {
		IReactor reactor = getReactor3x3(world, x, y, z);
		if (reactor != null)
			return reactor.getPosition();
		return null;
	}

	public static int euToSteam(int eu) {
		return (int) Math.floor((eu) * STEAM_PER_EU);
	}

	/*public static boolean isSteam(IReactor reactor) {
		return CrossModLoader.getCrossMod(ModIDs.IC2).isSteamReactor((TileEntity) reactor);
	}*/
}
