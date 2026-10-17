package com.zuxelus.zlib.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public abstract class FacingBlockSmall extends FacingBlock {

	public FacingBlockSmall(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return canPlaceAt(level, pos, state.getValue(FACING).getOpposite());
	}

	public static boolean canPlaceAt(LevelReader level, BlockPos pos, Direction direction) {
		BlockPos blockPos = pos.relative(direction);
		return level.getBlockState(blockPos).isFaceSturdy(level, blockPos, direction.getOpposite());
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction[] directions = ctx.getNearestLookingDirections();

		for (Direction direction : directions) {
			BlockState state = defaultBlockState().setValue(FACING, direction.getOpposite());
			if (state.canSurvive(ctx.getLevel(), ctx.getClickedPos())) {
				Player placer = ctx.getPlayer();
				if (placer == null)
					rotation = ctx.getHorizontalDirection();
				else if (placer.getXRot() <= -65)
					rotation = placer.getDirection();
				else
					rotation = placer.getDirection().getOpposite();
				return state;
			}
		}
		return null;
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return state.getValue(FACING).getOpposite() == facing && !state.canSurvive(level, currentPos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, facing, facingState, level, currentPos, facingPos);
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
