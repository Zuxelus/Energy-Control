package com.zuxelus.zlib.blocks;

import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class FacingHorizontal extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	public FacingHorizontal(AbstractBlock.Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}

	protected abstract BlockEntityFacing newBlockEntity();

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		BlockEntityFacing be = newBlockEntity();
		return be;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof BlockEntityFacing)
			((BlockEntityFacing) be).setFacing(state.get(FACING).getId());
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return getDefaultState().with(FACING, context.getPlayer().getHorizontalFacing().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state;
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity te = world.getBlockEntity(pos);
			if (te instanceof Inventory) {
				ItemScatterer.spawn(world, pos, (Inventory) te);
				world.updateComparators(pos, this);
			}
			super.onStateReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
}
