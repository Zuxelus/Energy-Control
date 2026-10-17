package com.zuxelus.energycontrol.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;

public class BlockLight extends Block {
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	public BlockLight(MapColor color) {
		super(Properties.of().mapColor(color).lightLevel(state -> state.getValue(LIT) ? 15 : 0).strength(1.0F, 3.0F).sound(SoundType.GLASS));
		registerDefaultState(getStateDefinition().any().setValue(LIT, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
		stateManager.add(LIT);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(LIT, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		if (level.isClientSide)
			return;

		boolean flag = state.getValue(LIT);
		if (flag == level.hasNeighborSignal(pos))
			return;

		if (flag)
			level.scheduleTick(pos, this, 4);
		else
			level.setBlock(pos, state.cycle(LIT), 2);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(LIT).booleanValue() && !level.hasNeighborSignal(pos))
			level.setBlock(pos, state.cycle(LIT), 2);
	}
}
