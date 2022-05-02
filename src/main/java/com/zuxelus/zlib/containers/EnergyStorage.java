package com.zuxelus.zlib.containers;

public class EnergyStorage { // copy from 1.10.2 net.minecraftforge.energy.EnergyStorage
	protected int energy;
	protected int capacity;
	protected int maxReceive;
	protected int maxExtract;

	public EnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		this.energy = energy;
	}

	public void setEnergy(int value) {
		energy = Math.min(value, capacity);
	}

	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!canReceive())
			return 0;

		int energyReceived = Math.min(capacity - energy, Math.min(maxReceive, maxReceive));
		if (!simulate)
			energy += energyReceived;
		return energyReceived;
	}

	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract())
			return 0;

		int energyExtracted = Math.min(energy, Math.min(maxExtract, maxExtract));
		if (!simulate)
			energy -= energyExtracted;
		return energyExtracted;
	}

	public int getEnergyStored() {
		return energy;
	}

	public int getMaxEnergyStored() {
		return capacity;
	}

	public boolean canExtract() {
		return maxExtract > 0;
	}

	public boolean canReceive() {
		return maxReceive > 0;
	}
}