package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityThermo;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class ThermalMonitor extends BlockBase {
	protected static final float[] AABB_DOWN = { 0.0625F, 0.5625F, 0.0625F, 0.9375F, 1.0F, 0.9375F };
	protected static final float[] AABB_UP = { 0.0625F, 0.0F, 0.0625F, 0.9375F, 0.4375F, 0.9375F };
	protected static final float[] AABB_NORTH = { 0.0625F, 0.0625F, 0.5625F, 0.9375F, 0.9375F, 1.0F };
	protected static final float[] AABB_SOUTH = { 0.0625F, 0.0625F, 0.0F, 0.9375F, 0.9375F, 0.4375F };
	protected static final float[] AABB_WEST = { 0.5625F, 0.0625F, 0.0625F, 1.0F, 0.9375F, 0.9375F };
	protected static final float[] AABB_EAST = { 0.0F, 0.0625F, 0.0625F, 0.4375F, 0.9375F, 0.9375F };
	private IIcon[] icons = new IIcon[6];

	public ThermalMonitor() {
		super(BlockDamages.DAMAGE_THERMAL_MONITOR, "thermal_monitor");
	}

	@Override
	public TileEntity createNewTileEntity() {
		TileEntityThermo te = new TileEntityThermo();
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
		icons[0] = registerIcon(iconRegister,"thermal_monitor/back");
		icons[1] = registerIcon(iconRegister,"thermal_monitor/face_green");
		icons[2] = registerIcon(iconRegister,"thermal_monitor/side_hor");
		icons[3] = registerIcon(iconRegister,"thermal_monitor/side_vert");
		icons[4] = registerIcon(iconRegister,"thermal_monitor/face_red");
		icons[5] = registerIcon(iconRegister,"thermal_monitor/face_gray");
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
