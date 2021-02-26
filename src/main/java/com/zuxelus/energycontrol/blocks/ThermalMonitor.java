package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityThermo;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ThermalMonitor extends FacingBlockSmall {
	protected static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.0625D, 0.5625D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
	protected static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.4375D, 0.9375D);
	protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0625D, 0.0625D, 0.5625D, 0.9375D, 0.9375D, 1.0D);
	protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.0625D, 0.0625D, 0.0D, 0.9375D, 0.9375D, 0.4375D);
	protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.5625D, 0.0625D, 0.0625D, 1.0D, 0.9375D, 0.9375D);
	protected static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.0D, 0.0625D, 0.0625D, 0.4375D, 0.9375D, 0.9375D);

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityThermo();
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		TileEntity te = blockAccess.getTileEntity(pos);
		if (!(te instanceof TileEntityThermo))
			return 0;
		return ((TileEntityThermo) te).getPowered() ? side != state.getValue(FACING) ? 15 : 0 : 0;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

		switch (enumfacing) {
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

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.ic2.isWrench(player.getHeldItem(hand)))
			return true;
		if (world.isRemote)
			player.openGui(EnergyControl.instance, getBlockGuiId(), world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_THERMAL_MONITOR;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
}
