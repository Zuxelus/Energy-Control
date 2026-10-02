package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.IndustrialAlarm;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityIndustrialAlarm extends TileEntityHowlerAlarm {

	private int lightLevel;
	private int updateLightTicker;

	public TileEntityIndustrialAlarm(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
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
		super.checkStatus();
		if (updateLightTicker-- <= 0) {
			updateLightTicker = 3;
			checkStatus();
		}
	}

	@Override
	protected void checkStatus() {
		if (level.isClientSide)
			return;
		int light = lightLevel;
		if (!powered)
			lightLevel = 0;
		else {
			lightLevel++;
			if (lightLevel >= 4)
				lightLevel = 0;
		}
		if (lightLevel != light) {
			BlockState state = level.getBlockState(worldPosition);
			if (state.getBlock() instanceof IndustrialAlarm)
				level.setBlock(worldPosition, state.setValue(IndustrialAlarm.LIGHT, lightLevel), 2);
		}
	}
}