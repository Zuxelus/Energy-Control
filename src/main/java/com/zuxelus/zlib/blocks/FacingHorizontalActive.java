package com.zuxelus.zlib.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;

public abstract class FacingHorizontalActive extends FacingHorizontal {

	public FacingHorizontalActive(Settings settings) {
		super(settings);
	}

	public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(ACTIVE);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return super.getPlacementState(context).with(ACTIVE, false);
	}
}
