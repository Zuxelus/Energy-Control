package com.zuxelus.zlib.containers;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class EnergyStorage extends SimpleEnergyHandler {

	public EnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
		super(capacity, maxReceive, maxExtract, energy);
	}

	public void setEnergy(int value) {
		energy = Math.max(0, Math.min(value, capacity));
	}

	public void setMax(int value) {
		maxInsert = value;
		maxExtract = value;
	}

	public int getEnergyStored() {
		return energy;
	}

	public int getMaxEnergyStored() {
		return capacity;
	}

	public int receiveEnergy(int amount, boolean simulate) {
		if (amount <= 0)
			return 0;
		try (Transaction tx = Transaction.openRoot()) {
			int result = insert(amount, tx);
			if (!simulate)
				tx.commit();
			return result;
		}
	}

	public int extractEnergy(int amount, boolean simulate) {
		if (amount <= 0)
			return 0;
		try (Transaction tx = Transaction.openRoot()) {
			int result = extract(amount, tx);
			if (!simulate)
				tx.commit();
			return result;
		}
	}
}
