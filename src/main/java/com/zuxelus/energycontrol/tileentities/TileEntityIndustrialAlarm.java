package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;

import net.minecraft.tileentity.TileEntityType;

public class TileEntityIndustrialAlarm extends TileEntityHowlerAlarm {
	private static final int[] lightSteps = { 0, 7, 14, 7, 0 };

	protected byte internalFire;
	public int lightLevel;
	private int updateLightTicker;

	public TileEntityIndustrialAlarm(TileEntityType<?> type) {
		super(type);
		internalFire = 0;
		lightLevel = 0;
	}

	public TileEntityIndustrialAlarm() {
		this(ModTileEntityTypes.industrial_alarm.get());
	}

	@Override
	public void tick() {
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