package com.zuxelus.energycontrol.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightBlock extends Block {
	public static final BooleanProperty LIT = Properties.LIT;

	public LightBlock() {
		super(FabricBlockSettings.of(Material.REDSTONE_LAMP).sounds(BlockSoundGroup.GLASS).lightLevel(state -> state.get(LIT) ? 15 : 0));
		setDefaultState(getStateManager().getDefaultState().with(LIT, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(LIT);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return (BlockState) getDefaultState().with(LIT, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		if (world.isClient)
			return;

		boolean lit = (Boolean) state.get(LIT);
		boolean power = world.isReceivingRedstonePower(pos);
		if (lit != power)
			world.setBlockState(pos, getDefaultState().with(LIT, power), 2);
	}
}
