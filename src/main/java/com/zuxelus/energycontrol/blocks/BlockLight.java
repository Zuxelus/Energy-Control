package com.zuxelus.energycontrol.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockLight extends Block {
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	public BlockLight() {
		super(Block.Properties.create(Material.REDSTONE_LIGHT).hardnessAndResistance(0.3F).sound(SoundType.GLASS));
		setDefaultState(getDefaultState().with(LIT, Boolean.valueOf(false)));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return state.get(LIT) ? 15 : 0;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(LIT, Boolean.valueOf(context.getWorld().isBlockPowered(context.getPos())));
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (world.isRemote)
			return;

		boolean flag = state.get(LIT);
		if (flag == world.isBlockPowered(pos))
			return;

		if (flag)
			world.getPendingBlockTicks().scheduleTick(pos, this, 4);
		else
			world.setBlockState(pos, state.func_235896_a_(LIT), 2);
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		if (state.get(LIT) && !world.isBlockPowered(pos))
			world.setBlockState(pos, state.func_235896_a_(LIT), 2);
	}
}
