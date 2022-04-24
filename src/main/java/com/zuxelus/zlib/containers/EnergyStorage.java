package com.zuxelus.zlib.containers;

public class EnergyStorage extends net.minecraftforge.energy.EnergyStorage {

	public EnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
		super(capacity, maxReceive, maxExtract);
		energy = 0;
	}

	public void setEnergy(int value) {
		energy = Math.min(value, capacity);
	}
}