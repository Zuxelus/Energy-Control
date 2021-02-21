package com.zuxelus.zlib.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public abstract class FacingBlockSmall extends FacingBlock {

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		return canPlaceAt(world, pos, state.get(FACING).getOpposite());
	}

	public static boolean canPlaceAt(IWorldReader world, BlockPos pos, Direction direction) {
		BlockPos blockPos = pos.offset(direction);
		return world.getBlockState(blockPos).isSolidSide(world, blockPos, direction.getOpposite());
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		Direction[] directions = ctx.getNearestLookingDirections();

		for (Direction direction : directions) {
			BlockState state = getDefaultState().with(FACING, direction.getOpposite());
			if (state.isValidPosition(ctx.getWorld(), ctx.getPos()))
				return state;
		}
		return null;
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return state.get(FACING).getOpposite() == facing && !state.isValidPosition(world, currentPos)
				? Blocks.AIR.getDefaultState()
				: super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}
}
