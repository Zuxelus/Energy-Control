package com.zuxelus.energycontrol.utils;

import com.zuxelus.energycontrol.api.ICardReader;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.core.registries.BuiltInRegistries;

public class FluidInfo {
	String displayName;
	String fluidId;
	long amount;
	long capacity;

	public FluidInfo(String name, String fluid, long amount, long capacity) {
		this.displayName = name;
		this.fluidId = fluid;
		this.amount = amount;
		this.capacity = capacity;
	}

	// amounts are in droplets (81000 per bucket), stored as mB
	public FluidInfo(StorageView<FluidVariant> stack) {
		if (stack == null)
			return;
		amount = stack.getAmount() / 81;
		FluidVariant variant = stack.getResource();
		if (amount > 0 && !variant.isBlank()) {
			// works on a dedicated server too: Language there is the server's built-in en_us
			displayName = FluidVariantAttributes.getName(variant).getString();
			fluidId = BuiltInRegistries.FLUID.getKey(variant.getFluid()).toString();
		}
		capacity = stack.getCapacity() / 81;
	}

	public void write(ICardReader reader) {
		reader.setString("name", displayName != null ? displayName : "");
		reader.setString("fluid", fluidId != null ? fluidId : "");
		reader.setLong("amount", amount);
		reader.setLong("capacity", capacity);
	}

	public void write(ICardReader reader, int i) {
		reader.setString(String.format("_%dname", i), displayName != null ? displayName : "");
		reader.setLong(String.format("_%damount", i), amount);
		reader.setLong(String.format("_%dcapacity", i), capacity);
	}
}
