package com.zuxelus.zlib.blocks;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.*;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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
import net.minecraft.world.World;

public abstract class FacingHorizontal extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	public FacingHorizontal(AbstractBlock.Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}

	protected abstract BlockEntityFacing newBlockEntity(BlockPos pos, BlockState state);

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		BlockEntityFacing be = newBlockEntity(pos, state);
		be.setFacing(state.get(FACING).getId());
		return be;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		if (type == ModTileEntityTypes.holo_panel)
			return checkType(type, type, TileEntityHoloPanel::tickStatic);
		if (type == ModTileEntityTypes.holo_panel_extender)
			return checkType(type, type, TileEntityHoloPanelExtender::tickStatic);
		if (type == ModTileEntityTypes.kit_assembler)
			return checkType(type, type, TileEntityKitAssembler::tickStatic);
		return null;
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
