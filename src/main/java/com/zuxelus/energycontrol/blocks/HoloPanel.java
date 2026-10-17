package com.zuxelus.energycontrol.blocks;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HoloPanel extends FacingHorizontalActive {
	protected static final VoxelShape AABB_NORTH = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 1.0D, 12.0D);
	protected static final VoxelShape AABB_WEST = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 1.0D, 16.0D);
	public static final MapCodec<HoloPanel> CODEC = simpleCodec(HoloPanel::new);

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	public HoloPanel(Properties properties) {
		super(FacingBlock.metal(properties));
	}

	@Override
	protected BlockEntityFacing createBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.holo_panel.get().create(pos, state);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (level.isClientSide())
			return;

		boolean flag = state.getValue(ACTIVE);
		if (flag == level.hasNeighborSignal(pos))
			return;

		if (flag)
			level.scheduleTick(pos, this, 4);
		else {
			level.setBlock(pos, state.cycle(ACTIVE), 2);
			updateExtenders(state, level, pos);
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(ACTIVE) && !level.hasNeighborSignal(pos)) {
			level.setBlock(pos, state.cycle(ACTIVE), 2);
			updateExtenders(state, level, pos);
		}
	}

	private void updateExtenders(BlockState state, Level level, BlockPos pos) { // server
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof TileEntityInfoPanel)
			((TileEntityInfoPanel) be).updateExtenders(level, !state.getValue(ACTIVE));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return canSurvive(state, level, currentPos) ? super.updateShape(state, facing, facingState, level, currentPos, facingPos) : Blocks.AIR.defaultBlockState();
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		BlockEntity te = level.getBlockEntity(pos);
		if (!(te instanceof TileEntityHoloPanel))
			return InteractionResult.PASS;
		if (!level.isClientSide())
			player.openMenu((TileEntityHoloPanel) te, buf -> buf.writeBlockPos(pos));
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(ACTIVE, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}
}
