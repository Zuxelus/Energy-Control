package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.IndustrialAlarm;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityIndustrialAlarm extends TileEntityHowlerAlarm {

	private int lightLevel;
	private int updateLightTicker;

	public TileEntityIndustrialAlarm(TileEntityType<?> type) {
		super(type);
		lightLevel = 0;
	}

	public TileEntityIndustrialAlarm() {
		this(ModTileEntityTypes.industrial_alarm.get());
	}

	@Override
	public void tick() {
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