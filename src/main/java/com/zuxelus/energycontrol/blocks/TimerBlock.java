package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.tileentities.TileEntityFacing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class TimerBlock extends FacingBlockSmall {
	protected static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.0625D, 0.5625D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
	protected static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.4375D, 0.9375D);
	protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0625D, 0.0625D, 0.5625D, 0.9375D, 0.9375D, 1.0D);
	protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.0625D, 0.0625D, 0.0D, 0.9375D, 0.9375D, 0.4375D);
	protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.5625D, 0.0625D, 0.0625D, 1.0D, 0.9375D, 0.9375D);
	protected static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.0D, 0.0625D, 0.0625D, 0.4375D, 0.9375D, 0.9375D);

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityTimer();
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		TileEntity te = blockAccess.getTileEntity(pos);
		if (!(te instanceof TileEntityTimer))
			return 0;
		return ((TileEntityTimer) te).getPowered() ? side != state.getValue(FACING) ? 15 : 0 : 0;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing enumfacing = state.getValue(FACING);

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
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_TIMER;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
}
