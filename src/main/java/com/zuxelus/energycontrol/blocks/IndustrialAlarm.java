package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.gui.ScreenHandler;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.energycontrol.tileentities.TileEntityIndustrialAlarm;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

public class IndustrialAlarm extends HowlerAlarm {
	public static final IntegerProperty LIGHT = IntegerProperty.create("light", 0, 3);
	private static final int[] lightSteps = { 0, 7, 14, 7, 0};

	public IndustrialAlarm() {
		super(Block.Properties.of(Material.METAL).strength(3.0F).lightLevel(state -> lightSteps[state.getValue(LIGHT)]));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(LIGHT);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(LIGHT, 0);
	}

	@Override
	protected BlockEntityFacing createBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.industrial_alarm.get().create(pos, state);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			BlockEntity te = world.getBlockEntity(pos);
			if (te instanceof TileEntityHowlerAlarm)
				ScreenHandler.openIndustrialAlarmScreen((TileEntityIndustrialAlarm) te);
		}
		return InteractionResult.SUCCESS;
	}
}
