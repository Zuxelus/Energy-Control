package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityTimer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TimerBlock extends FacingBlock {
	protected static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.0625D, 0.5625D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
	protected static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.4375D, 0.9375D);
	protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0625D, 0.0625D, 0.5625D, 0.9375D, 0.9375D, 1.0D);
	protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.0625D, 0.0625D, 0.0D, 0.9375D, 0.9375D, 0.4375D);
	protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.5625D, 0.0625D, 0.0625D, 1.0D, 0.9375D, 0.9375D);
	protected static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.0D, 0.0625D, 0.0625D, 0.4375D, 0.9375D, 0.9375D);

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityTimer te = new TileEntityTimer();
		te.setRotation(0);
		te.setFacing(meta);
		return te;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		return canPlaceBlock(worldIn, pos, side.getOpposite());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		for (EnumFacing enumfacing : EnumFacing.values()) {
			if (canPlaceBlock(worldIn, pos, enumfacing))
				return true;
		}
		return false;
	}

	protected static boolean canPlaceBlock(World worldIn, BlockPos pos, EnumFacing direction) {
		BlockPos blockpos = pos.offset(direction);
		return worldIn.getBlockState(blockpos).isSideSolid(worldIn, blockpos, direction.getOpposite());
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return canPlaceBlock(world, pos, facing.getOpposite()) ? getDefaultState().withProperty(FACING, facing) : getDefaultState().withProperty(FACING, EnumFacing.DOWN);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityTimer)
			switch (((TileEntityTimer) te).getFacing()) {
			case UP:
			case DOWN:
				((TileEntityTimer) te).setRotation(placer.getHorizontalFacing().getOpposite());
				break;
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST:
				((TileEntityTimer) te).setRotation(EnumFacing.DOWN);
				break;
			}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (checkForDrop(world, pos, state) && !canPlaceBlock(world, pos, ((EnumFacing) state.getValue(FACING)).getOpposite())) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	private boolean checkForDrop(World world, BlockPos pos, IBlockState state) {
		if (canPlaceBlockAt(world, pos))
			return true;
		dropBlockAsItem(world, pos, state, 0);
		world.setBlockToAir(pos);
		return false;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		TileEntity te = blockAccess.getTileEntity(pos);
		if (!(te instanceof TileEntityTimer))
			return 0;
		return ((TileEntityTimer) te).getPowered() ? side != blockState.getValue(FACING) ? 15 : 0 : 0;
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
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_TIMER;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
}