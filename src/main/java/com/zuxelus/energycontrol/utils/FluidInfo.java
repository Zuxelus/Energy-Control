package com.zuxelus.energycontrol.utils;

import com.zuxelus.energycontrol.api.ICardReader;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FluidInfo {
	String name; // display name, translated where the info is created
	String fluid; // fluid id; the client looks up its texture and tint color for the bar
	long amount;
	long capacity;

	public FluidInfo(String name, String fluid, long amount, long capacity) {
		this.name = name;
		this.fluid = fluid;
		this.amount = amount;
		this.capacity = capacity;
	}

	/*public FluidInfo(IFluidTank tank) {
		if (tank.getFluid() != null) {
			amount = tank.getFluidAmount();
			if (amount > 0) {
				translationKey = tank.getFluid().getTranslationKey();
				texture = tank.getFluid().getFluid().getAttributes().getStillTexture().toString();
				color = tank.getFluid().getFluid().getAttributes().getColor();
			}
		}
		capacity = tank.getCapacity();
	}*/

	public FluidInfo(FluidVolume stack, long capacity) {
		if (stack != null) {
			amount = stack.amount().whole;
			if (amount > 0) {
				/*translationKey = stack.getTranslationKey();
				texture = stack.fluidKey.getRawFluid().getAttributes().getStillTexture().toString();
				color = stack.fluidKey.getRawFluid().getAttributes().getColor();*/
			}
		}
		this.capacity = capacity;
	}

	public FluidInfo(SingleSlotStorage<FluidVariant> stack) {
		if (stack != null) {
			amount = stack.getAmount() / 81;
			FluidVariant variant = stack.getResource();
			if (amount > 0 && !variant.isBlank()) {
				// works on a dedicated server too: Language there is the server's built-in en_us
				name = FluidVariantAttributes.getName(variant).getString();
				fluid = Registries.FLUID.getId(variant.getFluid()).toString();
			}
		}
		this.capacity = stack.getCapacity() / 81;
	}

	public void write(ICardReader reader) {
		reader.setString("name", name != null ? name : "");
		reader.setString("fluid", fluid != null ? fluid : "");
		reader.setLong("amount", amount);
		reader.setLong("capacity", capacity);
	}

	public void write(ICardReader reader, int i) {
		reader.setString(String.format("_%dname", i), name != null ? name : "");
		reader.setLong(String.format("_%damount", i), amount);
		reader.setLong(String.format("_%dcapacity", i), capacity);
	}
}
