package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class Timer extends BlockBase {
	protected static final float[] AABB_DOWN = { 0.0625F, 0.5625F, 0.0625F, 0.9375F, 1.0F, 0.9375F };
	protected static final float[] AABB_UP = { 0.0625F, 0.0F, 0.0625F, 0.9375F, 0.4375F, 0.9375F };
	protected static final float[] AABB_NORTH = { 0.0625F, 0.0625F, 0.5625F, 0.9375F, 0.9375F, 1.0F };
	protected static final float[] AABB_SOUTH = { 0.0625F, 0.0625F, 0.0F, 0.9375F, 0.9375F, 0.4375F };
	protected static final float[] AABB_WEST = { 0.5625F, 0.0625F, 0.0625F, 1.0F, 0.9375F, 0.9375F };
	protected static final float[] AABB_EAST = { 0.0F, 0.0625F, 0.0625F, 0.4375F, 0.9375F, 0.9375F };
	private IIcon[] icons = new IIcon[4];

	public Timer() {
		super(BlockDamages.DAMAGE_TIMER, "timer");
	}

	@Override
	public TileEntity createNewTileEntity() {
		TileEntityTimer te = new TileEntityTimer();
		te.setFacing(0);
		te.setRotation(0);
		return te;
	}

	@Override
	public IIcon getIconFromSide(int side) {
		if (side > 3)
			return icons[2];
		return icons[side];
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		icons[0] = registerIcon(iconRegister,"timer/back");
		icons[1] = registerIcon(iconRegister,"timer/face");
		icons[2] = registerIcon(iconRegister,"timer/side_hor");
		icons[3] = registerIcon(iconRegister,"timer/side_vert");
	}

	@Override
	public boolean isSolidBlockRequired() {
		return true;
	}

	@Override
	public float[] getBlockBounds(TileEntity te) {
		if (!(te instanceof TileEntityFacing))
			return AABB_UP;
		switch (((TileEntityFacing) te).getFacingForge()) {
		case EAST:
			return AABB_EAST;
		case WEST:
			return AABB_WEST;
		case SOUTH:
			return AABB_SOUTH;
		case NORTH:
		default:
			return AABB_NORTH;
		case UP:
			return AABB_UP;
		case DOWN:
			return AABB_DOWN;
		}
	}
}
