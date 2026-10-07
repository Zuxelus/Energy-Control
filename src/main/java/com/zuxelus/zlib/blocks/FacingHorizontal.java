package com.zuxelus.zlib.blocks;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.*;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class FacingHorizontal extends BaseEntityBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

	public FacingHorizontal(BlockBehaviour.Properties settings) {
		super(settings);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	protected abstract BlockEntityFacing createFacingBlockEntity(BlockPos pos, BlockState state);

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		BlockEntityFacing be = createFacingBlockEntity(pos, state);
		be.setFacing(state.getValue(FACING).get3DDataValue());
		return be;
	}

	protected InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return InteractionResult.PASS;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		InteractionResult result = onUse(state, world, pos, player, hand, hit);
		return result == InteractionResult.PASS ? InteractionResult.TRY_WITH_EMPTY_HAND : result;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		return onUse(state, world, pos, player, InteractionHand.MAIN_HAND, hit);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		if (type == ModTileEntityTypes.holo_panel)
			return createTickerHelper(type, type, TileEntityHoloPanel::tickStatic);
		if (type == ModTileEntityTypes.holo_panel_extender)
			return createTickerHelper(type, type, TileEntityHoloPanelExtender::tickStatic);
		if (type == ModTileEntityTypes.kit_assembler)
			return createTickerHelper(type, type, TileEntityKitAssembler::tickStatic);
		if (type == ModTileEntityTypes.range_trigger)
			return createTickerHelper(type, type, TileEntityRangeTrigger::tickStatic);
		if (type == ModTileEntityTypes.remote_thermo)
			return createTickerHelper(type, type, TileEntityRemoteThermalMonitor::tickStatic);
		return null;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getPlayer().getDirection().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state;
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
