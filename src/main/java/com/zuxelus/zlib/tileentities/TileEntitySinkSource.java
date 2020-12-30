package com.zuxelus.zlib.tileentities;

import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntitySinkSource extends TileEntityEnergySource implements IEnergySink {

	public TileEntitySinkSource(String name, int tier, int output, int capacity) {
		super(name, tier, output, capacity);
	}

	// IEnergySink
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection dir) {
		return dir != getFacingForge();
	}

	@Override
	public double getDemandedEnergy() {
		return Math.min(capacity - energy, output);
	}

	@Override
	public int getSinkTier() {
		return tier;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		if (energy >= capacity)
			return amount;
		energy += amount;
		return 0.0D;
	}
}
