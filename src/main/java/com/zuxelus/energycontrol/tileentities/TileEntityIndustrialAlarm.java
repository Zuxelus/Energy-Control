package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityIndustrialAlarm extends TileEntityHowlerAlarm {
	private static final int[] lightSteps = { 0, 7, 14, 7, 0 };

	protected byte internalFire;
	public int lightLevel;
	private int updateLightTicker;

	public TileEntityIndustrialAlarm(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		internalFire = 0;
		lightLevel = 0;
	}

	public TileEntityIndustrialAlarm(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.industrial_alarm.get(), pos, state);
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
				updateLightTicker = tickRate / 20;
				checkStatus();
			}
		}
	}

	@Override
	protected void checkStatus() {
		int light = lightLevel;
		if (!powered) {
			lightLevel = 0;
			internalFire = 0;
		} else {
			lightLevel = lightSteps[internalFire];
			internalFire++;
			if (internalFire >= lightSteps.length)
				internalFire = 0;
		}
		if (lightLevel != light)
			level.getChunkSource().getLightEngine().checkBlock(worldPosition);
	}
}