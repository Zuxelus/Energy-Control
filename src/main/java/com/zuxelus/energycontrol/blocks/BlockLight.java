package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.init.ModItems;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BlockLight extends Block {
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	public BlockLight() {
		super(ModItems.emptyBlockSettings().lightLevel(state -> state.getValue(LIT) ? 15 : 0).strength(0.3F).sound(SoundType.GLASS));
		registerDefaultState(getStateDefinition().any().setValue(LIT, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
		stateManager.add(LIT);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(LIT, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
	}

	@Override
	protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, @Nullable Orientation orientation, boolean moved) {
		if (world.isClientSide())
			return;

		boolean flag = state.getValue(LIT);
		if (flag == world.hasNeighborSignal(pos))
			return;

		if (flag)
			world.scheduleTick(pos, this, 4);
		else
			world.setBlock(pos, state.cycle(LIT), 2);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (state.getValue(LIT).booleanValue() && !world.hasNeighborSignal(pos))
			world.setBlock(pos, state.cycle(LIT), 2);
	}
}
