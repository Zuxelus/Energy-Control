package com.zuxelus.energycontrol.blocks;

import org.jspecify.annotations.Nullable;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TimerBlock extends FacingBlockSmall {
	protected static final VoxelShape AABB_DOWN = Block.box(1.0F, 9.0F, 1.0F, 15.0F, 15.0F, 15.0F);
	protected static final VoxelShape AABB_UP = Block.box(1.0F, 0.0F, 1.0F, 15.0F, 7.0F, 15.0F);
	protected static final VoxelShape AABB_NORTH = Block.box(1.0F, 1.0F, 9.0F, 15.0F, 15.0F, 15.0F);
	protected static final VoxelShape AABB_SOUTH = Block.box(1.0F, 1.0F, 0.0F, 15.0F, 15.0F, 7.0F);
	protected static final VoxelShape AABB_WEST = Block.box(9.0F, 1.0F, 1.0F, 15.0F, 15.0F, 15.0F);
	protected static final VoxelShape AABB_EAST = Block.box(0.0F, 1.0F, 1.0F, 7.0F, 15.0F, 15.0F);

	public TimerBlock() {
		super(ModItems.blockSettings());
	}

	@Override
	protected BlockEntityFacing createFacingBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.timer.create(pos, state);
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		if (!movedByPiston)
			level.updateNeighborsAt(pos, this);
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
		BlockEntity te = blockAccess.getBlockEntity(pos);
		if (!(te instanceof TileEntityTimer))
			return 0;
		if (side == state.getValue(FACING) || side == ((TileEntityTimer) te).getRotation().getOpposite())
			return 0;
		return ((TileEntityTimer) te).getPowered() ? 15 : 0;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		switch (state.getValue(FACING)) {
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
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	protected InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide())
			return InteractionResult.PASS;
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityTimer))
			return InteractionResult.PASS;
		player.openMenu(state.getMenuProvider(world, pos));
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block fromBlock, @Nullable Orientation orientation, boolean isMoving) {
		if (!level.isClientSide()) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof TileEntityTimer)
				((TileEntityTimer) be).onNeighborChange(fromBlock);
		}
	}
}
