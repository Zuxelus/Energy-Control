package com.zuxelus.zlib.containers;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class EnergyStorage extends SimpleEnergyStorage {

	public EnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
		super(capacity, maxReceive, maxExtract);
		amount = energy;
	}

	public void setEnergy(long value) {
		amount = Math.min(value, capacity);
	}

	public long insert(long amount, boolean simulate) {
		long amountInserted = 0;
		try (Transaction transaction = Transaction.openNested(null)) {
			amountInserted = insert(amount, transaction);
			if (!simulate)
				transaction.commit();
		}
		return amountInserted;
	}

	public long extract(long amount, boolean simulate) {
		long amountExtracted = 0;
		try (Transaction transaction = Transaction.openNested(null)) {
			amountExtracted = extract(amount, transaction);
			if (!simulate)
				transaction.commit();
		}
		return amountExtracted;
	}
}