package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.IndustrialAlarm;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityIndustrialAlarm extends TileEntityHowlerAlarm {
	private int lightStep;
	private int updateLightTicker;

	public TileEntityIndustrialAlarm(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		lightStep = 0;
	}

	public TileEntityIndustrialAlarm(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.industrial_alarm, pos, state);
	}

	public static void tickStatic(Level level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityIndustrialAlarm))
			return;
		TileEntityIndustrialAlarm te = (TileEntityIndustrialAlarm) be;
		te.tick();
	}

	protected void tick() {
		if (level.isClientSide) {
			super.checkStatus();
			if (updateLightTicker-- <= 0) {
				updateLightTicker = 3;
				checkStatus();
			}
		}
	}

	// The light is only a decoration, so every client animates it on its own and the server never changes the block.
	// The step is written into the block state (not cycled), so a state reset by the server or a switched off alarm always ends up right
	@Override
	protected void checkStatus() {
		if (!powered)
			lightStep = 0;
		else if (++lightStep >= IndustrialAlarm.lightSteps.length)
			lightStep = 0;

		BlockState state = level.getBlockState(worldPosition);
		if (state.getBlock() instanceof IndustrialAlarm && state.getValue(IndustrialAlarm.LIGHT) != lightStep) {
			level.setBlock(worldPosition, state.setValue(IndustrialAlarm.LIGHT, lightStep), 2);
			level.getChunkSource().getLightEngine().checkBlock(worldPosition);
		}
	}
}
