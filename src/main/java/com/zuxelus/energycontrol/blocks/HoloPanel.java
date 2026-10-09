package com.zuxelus.energycontrol.blocks;

import org.jspecify.annotations.Nullable;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.blocks.FacingHorizontalActive;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HoloPanel extends FacingHorizontalActive {
	protected static final VoxelShape AABB_NORTH = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 1.0D, 12.0D);
	protected static final VoxelShape AABB_WEST = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 1.0D, 16.0D);

	public HoloPanel(Block.Properties properties) {
		super(FacingBlock.metal(properties));
	}

	@Override
	protected BlockEntityFacing createBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.holo_panel.get().create(pos, state);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP);
	}

	@Override
	protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
		if (world.isClientSide())
			return;

		boolean flag = state.getValue(ACTIVE);
		if (flag == world.hasNeighborSignal(pos))
			return;

		if (flag)
			world.scheduleTick(pos, this, 4);
		else {
			world.setBlock(pos, state.cycle(ACTIVE), 2);
			updateExtenders(state, world, pos);
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (state.getValue(ACTIVE) && !world.hasNeighborSignal(pos)) {
			world.setBlock(pos, state.cycle(ACTIVE), 2);
			updateExtenders(state, world, pos);
		}
	}

	private void updateExtenders(BlockState state, Level world, BlockPos pos) { // server
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof TileEntityInfoPanel)
			((TileEntityInfoPanel) be).updateExtenders(world, !state.getValue(ACTIVE));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		switch (state.getValue(FACING)) {
		case WEST:
		case EAST:
			return AABB_WEST;
		case NORTH:
		case SOUTH:
		default:
			return AABB_NORTH;
		}
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess ticks, BlockPos currentPos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
		return canSurvive(state, world, currentPos) ? super.updateShape(state, world, ticks, currentPos, facing, facingPos, facingState, random) : Blocks.AIR.defaultBlockState();
	}

	@Override
	protected InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityHoloPanel))
			return InteractionResult.PASS;
		if (!world.isClientSide())
			player.openMenu((TileEntityHoloPanel) te, buf -> buf.writeBlockPos(pos));
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(ACTIVE, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}
}
