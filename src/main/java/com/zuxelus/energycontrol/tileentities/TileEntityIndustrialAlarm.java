package com.zuxelus.energycontrol.tileentities;

public class TileEntityIndustrialAlarm extends TileEntityHowlerAlarm {
	private static final int[] lightSteps = { 0, 7, 14, 7, 0 };

	protected byte internalFire;
	public int lightLevel;
	private int updateLightTicker;

	public TileEntityIndustrialAlarm() {
		super();
		internalFire = 0;
		lightLevel = 0;
	}

	@Override
	public void update() {
		if (world.isRemote) {
			if (updateTicker-- <= 0) {
				updateTicker = tickRate;
				super.checkStatus();
			}
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
			world.checkLight(pos);
	}
}