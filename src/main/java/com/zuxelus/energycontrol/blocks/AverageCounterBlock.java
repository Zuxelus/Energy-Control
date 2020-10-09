package com.zuxelus.energycontrol.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.world.BlockView;

public class AverageCounterBlock extends FacingBlock implements BlockEntityProvider {

	public AverageCounterBlock() {
		super(FabricBlockSettings.of(Material.METAL).strength(12.0F));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		// TODO Auto-generated method stub
		return null;
	}

}
