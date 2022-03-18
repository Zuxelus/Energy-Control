package com.zuxelus.zlib.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;

public abstract class FacingBlockActive extends FacingBlock {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public FacingBlockActive(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(ACTIVE, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ACTIVE);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(ACTIVE, false);
	}
}
