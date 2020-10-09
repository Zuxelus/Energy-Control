package com.zuxelus.energycontrol.blockentities;

import com.zuxelus.energycontrol.blocks.IndustrialAlarmBlock;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.block.BlockState;

public class IndustrialAlarmBlockEntity extends HowlerAlarmBlockEntity {
	private int updateLightTicker;

	public IndustrialAlarmBlockEntity() {
		super(ModItems.INDUSTRIAL_ALARM_BLOCK_ENTITY);
		updateLightTicker = tickRate / 20;
	}

	@Override
	public void tick() {
		super.tick();
		if (!world.isClient && updateLightTicker-- <= 0) {
			updateLightTicker = tickRate / 20;
			checkStatus();
		}
	}

	@Override
	protected void checkStatus() {
		if (world.isClient) {
			super.checkStatus();
			return;
		}
		BlockState state = world.getBlockState(pos);
		int light = state.get(IndustrialAlarmBlock.LIGHT);
		if (!powered) {
			if (light != 0) {
				world.setBlockState(pos, state.with(IndustrialAlarmBlock.LIGHT, 0), 3);
				world.updateNeighborsAlways(pos, state.getBlock());
			}
		} else {
			world.setBlockState(pos, state.cycle(IndustrialAlarmBlock.LIGHT), 3);
			world.updateNeighborsAlways(pos, state.getBlock());
		}
	}
}
