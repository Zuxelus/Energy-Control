package com.zuxelus.energycontrol.blocks;

import java.util.Random;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLight extends Block {
	public static final BooleanProperty LIT = Properties.LIT;

	public BlockLight() {
		super(FabricBlockSettings.of(Material.REDSTONE_LAMP).luminance(state -> state.get(LIT) ? 15 : 0).strength(0.3F).sounds(BlockSoundGroup.GLASS));
		setDefaultState(getStateManager().getDefaultState().with(LIT, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(LIT);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(LIT, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		if (world.isClient)
			return;

		boolean flag = state.get(LIT);
		if (flag == world.isReceivingRedstonePower(pos))
			return;

		if (flag)
			world.createAndScheduleBlockTick(pos, this, 4);
		else
			world.setBlockState(pos, state.cycle(LIT), 2);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (state.get(LIT).booleanValue() && !world.isReceivingRedstonePower(pos))
			world.setBlockState(pos, state.cycle(LIT), 2);
	}
}
