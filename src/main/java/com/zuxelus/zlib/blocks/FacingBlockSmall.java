package com.zuxelus.zlib.blocks;

import com.zuxelus.zlib.tileentities.TileEntityFacing;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class FacingBlockSmall extends FacingBlock {

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return canPlaceBlock(world, pos, side.getOpposite());
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		for (EnumFacing enumfacing : EnumFacing.values()) {
			if (canPlaceBlock(world, pos, enumfacing))
				return true;
		}
		return false;
	}

	protected static boolean canPlaceBlock(World world, BlockPos pos, EnumFacing direction) { // TODO
		BlockPos blockpos = pos.offset(direction);
		return world.getBlockState(blockpos).isSideSolid(world, blockpos, direction.getOpposite());
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		return canPlaceBlock(world, pos, facing.getOpposite()) ? getDefaultState().withProperty(FACING, facing) : getDefaultState().withProperty(FACING, EnumFacing.DOWN);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFacing)
			switch (((TileEntityFacing) te).getFacing()) {
			case UP:
			case DOWN:
				((TileEntityFacing) te).setRotation(placer.getHorizontalFacing().getOpposite());
				break;
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST:
				((TileEntityFacing) te).setRotation(EnumFacing.DOWN);
				break;
			}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
		if (checkForDrop(world, pos, state) && !canPlaceBlock(world, pos, state.getValue(FACING).getOpposite())) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	protected boolean checkForDrop(World world, BlockPos pos, IBlockState state) { // TODO
		if (canPlaceBlockAt(world, pos))
			return true;
		dropBlockAsItem(world, pos, state, 0);
		world.setBlockToAir(pos);
		return false;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}
}
