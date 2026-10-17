package com.zuxelus.zlib.blocks;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.*;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class FacingBlock extends BaseEntityBlock {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	protected Direction rotation;

	public FacingBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	protected abstract BlockEntityFacing createFacingBlockEntity(BlockPos pos, BlockState state);

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		BlockEntityFacing be = createFacingBlockEntity(pos, state);
		be.setFacing(state.getValue(FACING).get3DDataValue());
		be.setRotation(rotation);
		return be;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (type == ModTileEntityTypes.info_panel)
			return createTickerHelper(type, type, TileEntityInfoPanel::tickStatic);
		if (type == ModTileEntityTypes.info_panel_extender)
			return createTickerHelper(type, type, TileEntityInfoPanelExtender::tickStatic);
		if (type == ModTileEntityTypes.info_panel_advanced)
			return createTickerHelper(type, type, TileEntityAdvancedInfoPanel::tickStatic);
		if (type == ModTileEntityTypes.info_panel_advanced_extender)
			return createTickerHelper(type, type, TileEntityAdvancedInfoPanelExtender::tickStatic);
		if (type == ModTileEntityTypes.howler_alarm)
			return createTickerHelper(type, type, TileEntityHowlerAlarm::tickStatic);
		if (type == ModTileEntityTypes.industrial_alarm)
			return createTickerHelper(type, type, TileEntityIndustrialAlarm::tickStatic);
		if (type == ModTileEntityTypes.thermal_monitor)
			return createTickerHelper(type, type, TileEntityThermalMonitor::tickStatic);
		if (type == ModTileEntityTypes.timer)
			return createTickerHelper(type, type, TileEntityTimer::tickStatic);
		return null;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Player placer = context.getPlayer();
		if (placer == null)
			return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
		if (placer.getXRot() >= 65) {
			rotation = placer.getDirection().getOpposite();
			return defaultBlockState().setValue(FACING, Direction.UP);
		}
		if (placer.getXRot() <= -65) {
			rotation = placer.getDirection();
			return defaultBlockState().setValue(FACING, Direction.DOWN);
		}
		rotation = Direction.DOWN;
		switch (Mth.floor(placer.getYRot() * 4.0F / 360.0F + 0.5D) & 3) {
		case 0:
			return defaultBlockState().setValue(FACING, Direction.NORTH);
		case 1:
			return defaultBlockState().setValue(FACING, Direction.EAST);
		case 2:
			return defaultBlockState().setValue(FACING, Direction.SOUTH);
		case 3:
			return defaultBlockState().setValue(FACING, Direction.WEST);
		}
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
	}

	// From 1.21.2, BlockEntity.preRemoveSideEffects drops a Container's contents by default
	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity te = level.getBlockEntity(pos);
			if (te instanceof Container) {
				Containers.dropContents(level, pos, (Container) te);
				level.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}
}
