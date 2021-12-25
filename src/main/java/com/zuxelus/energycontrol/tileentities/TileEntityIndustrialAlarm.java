package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.IndustrialAlarm;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
		this(ModTileEntityTypes.industrial_alarm, pos, state);
	}

	public static void tickStatic(World level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityIndustrialAlarm))
			return;
		TileEntityIndustrialAlarm te = (TileEntityIndustrialAlarm) be;
		te.tick();
	}

	protected void tick() {
		if (world.isClient) {
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
		BlockState state = world.getBlockState(pos);
		if (!powered) {
			lightLevel = 0;
			internalFire = 0;
		} else {
			lightLevel = lightSteps[internalFire];
			internalFire++;
			if (internalFire >= lightSteps.length)
				internalFire = 0;
		}
		
		if (lightLevel != light) {
			world.setBlockState(pos, state.cycle(IndustrialAlarm.LIGHT), 3);
			world.getChunkManager().getLightingProvider().checkBlock(pos);
		}
	}
}