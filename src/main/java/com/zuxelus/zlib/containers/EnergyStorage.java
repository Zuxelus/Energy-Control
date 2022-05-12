package com.zuxelus.zlib.containers;

public class EnergyStorage { // copy from 1.10.2 net.minecraftforge.energy.EnergyStorage
	protected long energy;
	protected long capacity;
	protected long maxReceive;
	protected long maxExtract;

	public EnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		this.energy = energy;
	}

	public void setEnergy(long value) {
		//energy = Math.min(value, capacity);
		energy = value;
	}

	public long receiveEnergy(long maxReceive, boolean simulate) {
		if (!canReceive())
			return 0;

		long energyReceived = Math.min(capacity - energy, Math.min(maxReceive, maxReceive));
		if (!simulate)
			energy += energyReceived;
		return energyReceived;
	}

	public long extractEnergy(long maxExtract, boolean simulate) {
		if (!canExtract())
			return 0;

		long energyExtracted = Math.min(energy, Math.min(maxExtract, maxExtract));
		if (!simulate)
			energy -= energyExtracted;
		return energyExtracted;
	}

	public long getEnergyStored() {
		return energy;
	}

	public long getMaxEnergyStored() {
		return capacity;
	}

	public boolean canExtract() {
		return maxExtract > 0;
	}

	public boolean canReceive() {
		return maxReceive > 0;
	}
}