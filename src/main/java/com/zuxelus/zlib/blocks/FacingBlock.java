package com.zuxelus.zlib.blocks;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.*;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class FacingBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.FACING;
	protected Direction rotation;

	public FacingBlock(AbstractBlock.Settings settings) {
		super(settings);
		//setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}

	protected abstract BlockEntityFacing newBlockEntity(BlockPos pos, BlockState state);

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		BlockEntityFacing be = newBlockEntity(pos, state);
		be.setFacing(state.get(FACING).getId());
		be.setRotation(rotation);
		return be;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		if (type == ModTileEntityTypes.info_panel)
			return checkType(type, type, TileEntityInfoPanel::tickStatic);
		if (type == ModTileEntityTypes.info_panel_extender)
			return checkType(type, type, TileEntityInfoPanelExtender::tickStatic);
		if (type == ModTileEntityTypes.info_panel_advanced)
			return checkType(type, type, TileEntityAdvancedInfoPanel::tickStatic);
		if (type == ModTileEntityTypes.info_panel_advanced_extender)
			return checkType(type, type, TileEntityAdvancedInfoPanelExtender::tickStatic);
		if (type == ModTileEntityTypes.howler_alarm)
			return checkType(type, type, TileEntityHowlerAlarm::tickStatic);
		if (type == ModTileEntityTypes.industrial_alarm)
			return checkType(type, type, TileEntityIndustrialAlarm::tickStatic);
		if (type == ModTileEntityTypes.timer)
			return checkType(type, type, TileEntityTimer::tickStatic);
		return null;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		PlayerEntity placer = context.getPlayer();
		rotation = placer.getHorizontalFacing().getOpposite();
		if (placer.getPitch() >= 65)
			return getDefaultState().with(FACING, Direction.UP);
		if (placer.getPitch() <= -65) {
			rotation = placer.getHorizontalFacing();
			return getDefaultState().with(FACING, Direction.DOWN);
		}
		switch (MathHelper.floor(placer.getYaw() * 4.0F / 360.0F + 0.5D) & 3) {
		case 0:
			return getDefaultState().with(FACING, Direction.NORTH);
		case 1:
			return getDefaultState().with(FACING, Direction.EAST);
		case 2:
			return getDefaultState().with(FACING, Direction.SOUTH);
		case 3:
			return getDefaultState().with(FACING, Direction.WEST);
		}
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite());
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
}
