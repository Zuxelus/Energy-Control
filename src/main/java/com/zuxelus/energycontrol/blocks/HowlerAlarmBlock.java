package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.blockentities.HowlerAlarmBlockEntity;
import com.zuxelus.energycontrol.gui.ScreenHandler;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class HowlerAlarmBlock extends FacingBlock implements BlockEntityProvider {
	protected static final VoxelShape AABB_DOWN = Block.createCuboidShape(2.0D, 9.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	protected static final VoxelShape AABB_UP = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);
	protected static final VoxelShape AABB_NORTH = Block.createCuboidShape(2.0D, 2.0D, 9.0D, 14.0D, 14.0D, 16.0D);
	protected static final VoxelShape AABB_SOUTH = Block.createCuboidShape(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 7.0D);
	protected static final VoxelShape AABB_WEST = Block.createCuboidShape(9.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
	protected static final VoxelShape AABB_EAST = Block.createCuboidShape(0.0D, 2.0D, 2.0D, 7.0D, 14.0D, 14.0D);

	public HowlerAlarmBlock() {
		super(FabricBlockSettings.of(Material.METAL).strength(12.0F));
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return canPlaceAt(world, pos, state.get(FACING).getOpposite());
	}

	public static boolean canPlaceAt(WorldView worldView, BlockPos pos, Direction direction) {
		BlockPos blockPos = pos.offset(direction);
		return worldView.getBlockState(blockPos).isSideSolidFullSquare(worldView, blockPos, direction.getOpposite());
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction[] directions = ctx.getPlacementDirections();

		for (Direction direction : directions) {
			BlockState state = getDefaultState().with(FACING, direction.getOpposite());
			if (state.canPlaceAt(ctx.getWorld(), ctx.getBlockPos()))
				return state;
		}
		return null;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new HowlerAlarmBlockEntity();
	}

	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		switch (state.get(FACING)) {
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
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof HowlerAlarmBlockEntity)
				ScreenHandler.openHowlerAlarmScreen((HowlerAlarmBlockEntity) be);
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		if (world.isClient)
			return;

		BlockState iblockstate = world.getBlockState(pos);
		world.updateListeners(pos, iblockstate, iblockstate, 2);
	}
}
