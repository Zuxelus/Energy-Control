package com.zuxelus.zlib.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class FacingHorizontal extends FacingBlock {

	@Override
	public int getMetaForPlacement(World world, int x, int y, int z, int facing, float hitX, float hitY, float hitZ, int meta, EntityPlayer placer) {
		switch (MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3) {
		default:
		case 0:
			return ForgeDirection.NORTH.ordinal();
		case 1:
			return ForgeDirection.EAST.ordinal();
		case 2:
			return ForgeDirection.SOUTH.ordinal();
		case 3:
			return ForgeDirection.WEST.ordinal();
		}
	}
}
