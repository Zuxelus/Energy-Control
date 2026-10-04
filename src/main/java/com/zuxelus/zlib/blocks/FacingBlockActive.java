package com.zuxelus.zlib.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;

public abstract class FacingBlockActive extends FacingBlock {
	public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

	public FacingBlockActive(AbstractBlock.Settings settings) {
		super(settings);
		//setDefaultState(getDefaultState().with(ACTIVE, false));
	}

	// wall panels use this ACTIVE, holo panels the one in FacingHorizontalActive: block state properties are compared by identity
	public static BooleanProperty getActive(BlockState state) {
		return state.contains(FacingHorizontalActive.ACTIVE) ? FacingHorizontalActive.ACTIVE : ACTIVE;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(ACTIVE);
	}

	/*@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return super.getPlacementState(context).with(ACTIVE, false);
	}*/
}
