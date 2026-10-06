package com.zuxelus.zlib.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class FacingBlockActive extends FacingBlock {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public FacingBlockActive(BlockBehaviour.Properties settings) {
		super(settings);
		//setDefaultState(getDefaultState().with(ACTIVE, false));
	}

	// wall panels use this ACTIVE, holo panels the one in FacingHorizontalActive: block state properties are compared by identity
	public static BooleanProperty getActive(BlockState state) {
		return state.hasProperty(FacingHorizontalActive.ACTIVE) ? FacingHorizontalActive.ACTIVE : ACTIVE;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ACTIVE);
	}

	/*@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return super.getPlacementState(context).with(ACTIVE, false);
	}*/
}
