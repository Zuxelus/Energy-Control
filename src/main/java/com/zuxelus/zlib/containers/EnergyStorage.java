package com.zuxelus.zlib.containers;

// a plain energy buffer: Team Reborn Energy 0.1.1 (1.16) has no storage implementation to extend
public class EnergyStorage {
	private final long capacity;
	private final long maxReceive;
	private final long maxExtract;
	private long amount;

	public EnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		amount = Math.min(energy, capacity);
	}

	public long getAmount() {
		return amount;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setEnergy(long value) {
		amount = Math.min(value, capacity);
	}

	public long insert(long amount, boolean simulate) {
		long amountInserted = Math.max(0, Math.min(Math.min(amount, maxReceive), capacity - this.amount));
		if (!simulate)
			this.amount += amountInserted;
		return amountInserted;
	}

	public long extract(long amount, boolean simulate) {
		long amountExtracted = Math.max(0, Math.min(Math.min(amount, maxExtract), this.amount));
		if (!simulate)
			this.amount -= amountExtracted;
		return amountExtracted;
	}
}
