package com.zuxelus.zlib.blocks;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class FacingBlockSmall extends FacingBlock {

	public FacingBlockSmall(Settings settings) {
		super(settings);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return canPlaceAt(world, pos, state.get(FACING).getOpposite());
	}

	public static boolean canPlaceAt(WorldView world, BlockPos pos, Direction direction) {
		BlockPos blockPos = pos.offset(direction);
		return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction.getOpposite());
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction[] directions = ctx.getPlacementDirections();

		for (Direction direction : directions) {
			BlockState state = getDefaultState().with(FACING, direction.getOpposite());
			if (state.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
				PlayerEntity placer = ctx.getPlayer();
				rotation = placer.getHorizontalFacing().getOpposite();
				if (placer.getPitch() <= -65)
					rotation = placer.getHorizontalFacing();
				return state;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess world, BlockPos currentPos, BlockPos facingPos) {
		return state.get(FACING).getOpposite() == facing && !state.canPlaceAt(world, currentPos)
				? Blocks.AIR.getDefaultState()
				: super.getStateForNeighborUpdate(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
}
