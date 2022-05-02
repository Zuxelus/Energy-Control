package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

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
	public void updateEntity() {
		if (worldObj.isRemote) {
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
			worldObj.func_147451_t(xCoord, yCoord, zCoord);
	}

	// IWrenchable
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player) {
		return new ItemStack(ModItems.blockIndustrialAlarm);
	}
}