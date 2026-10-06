package com.zuxelus.zlib.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public abstract class FacingBlockSmall extends FacingBlock {

	public FacingBlockSmall(Properties settings) {
		super(settings);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return canPlaceAt(world, pos, state.getValue(FACING).getOpposite());
	}

	public static boolean canPlaceAt(LevelReader world, BlockPos pos, Direction direction) {
		BlockPos blockPos = pos.relative(direction);
		return world.getBlockState(blockPos).isFaceSturdy(world, blockPos, direction.getOpposite());
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction[] directions = ctx.getNearestLookingDirections();

		for (Direction direction : directions) {
			BlockState state = defaultBlockState().setValue(FACING, direction.getOpposite());
			if (state.canSurvive(ctx.getLevel(), ctx.getClickedPos())) {
				Player placer = ctx.getPlayer();
				rotation = placer.getDirection().getOpposite();
				if (placer.getXRot() <= -65)
					rotation = placer.getDirection();
				return state;
			}
		}
		return null;
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess ticks, BlockPos currentPos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
		return state.getValue(FACING).getOpposite() == facing && !state.canSurvive(world, currentPos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, world, ticks, currentPos, facing, facingPos, facingState, random);
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
