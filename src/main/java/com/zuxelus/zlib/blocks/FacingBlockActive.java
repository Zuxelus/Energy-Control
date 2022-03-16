package com.zuxelus.zlib.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;

public abstract class FacingBlockActive extends FacingBlock {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public FacingBlockActive(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ACTIVE);
	}
}
