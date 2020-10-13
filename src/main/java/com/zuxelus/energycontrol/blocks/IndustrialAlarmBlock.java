package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.blockentities.IndustrialAlarmBlockEntity;
import com.zuxelus.energycontrol.screen.ScreenHandler;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class IndustrialAlarmBlock extends HowlerAlarmBlock {
	public static final IntProperty LIGHT = IntProperty.of("light", 0, 3);
	private static final int[] lightSteps = { 0, 7, 14, 7, 0};

	public IndustrialAlarmBlock() {
		super(FabricBlockSettings.of(Material.METAL).strength(12.0F).lightLevel(state -> lightSteps[state.get(LIGHT)]));
		setDefaultState(getDefaultState().with(LIGHT, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIGHT);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new IndustrialAlarmBlockEntity();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof IndustrialAlarmBlockEntity)
				ScreenHandler.openIndustrialAlarmScreen((IndustrialAlarmBlockEntity) be);
		}
		return ActionResult.SUCCESS;
	}
}
