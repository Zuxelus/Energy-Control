package com.zuxelus.energycontrol.tileentities;

import net.minecraft.core.Direction;

public interface IEnergyBlockEntity {

	boolean canAcceptEnergy(Direction side);

	int getRequestedEnergy();

	int getSinkTier();

	int acceptEnergy(Direction directionFrom, int amount, int voltage);

	boolean canEmitEnergy(Direction side);

	void consumeEnergy(int amount);

	int getMaxEnergyOutput();

	int getProvidedEnergy();

	int getSourceTier();
}
