package com.zuxelus.zlib.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public abstract class FacingBlockSmall extends FacingBlock {

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		return canPlaceAt(world, pos, state.getValue(FACING).getOpposite());
	}

	public static boolean canPlaceAt(IWorldReader world, BlockPos pos, Direction direction) {
		BlockPos blockPos = pos.relative(direction);
		return world.getBlockState(blockPos).isFaceSturdy(world, blockPos, direction.getOpposite());
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		Direction[] directions = ctx.getNearestLookingDirections();

		for (Direction direction : directions) {
			BlockState state = defaultBlockState().setValue(FACING, direction.getOpposite());
			if (state.canSurvive(ctx.getLevel(), ctx.getClickedPos())) {
				PlayerEntity placer = ctx.getPlayer();
				rotation = placer.getDirection().getOpposite();
				if (placer.xRot <= -65)
					rotation = placer.getDirection();
				return state;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return state.getValue(FACING).getOpposite() == facing && !state.canSurvive(world, currentPos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}
}
