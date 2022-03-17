package com.zuxelus.zlib.blocks;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;

public abstract class FacingHorizontalActive extends FacingHorizontal {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public FacingHorizontalActive() {
		super(Block.Properties.of(Material.METAL).strength(3.0F));
		registerDefaultState(defaultBlockState().setValue(ACTIVE, false));
	}

	public FacingHorizontalActive(Block.Properties builder) {
		super(builder);
		registerDefaultState(defaultBlockState().setValue(ACTIVE, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ACTIVE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(ACTIVE, false);
	}
}
