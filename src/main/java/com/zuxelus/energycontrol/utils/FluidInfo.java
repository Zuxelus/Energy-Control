package com.zuxelus.energycontrol.utils;

import com.zuxelus.energycontrol.api.ICardReader;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class FluidInfo {
	String translationKey;
	String fluidId;
	long amount;
	long capacity;

	public FluidInfo(String translationKey, String fluidId, long amount, long capacity) {
		this.translationKey = translationKey;
		this.fluidId = fluidId;
		this.amount = amount;
		this.capacity = capacity;
	}

	public FluidInfo(FluidStack stack, long capacity) {
		if (stack != null) {
			amount = stack.getAmount();
			if (amount > 0) {
				translationKey = stack.getDescriptionId();
				fluidId = BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString();
			}
		}
		this.capacity = capacity;
	}

	public FluidInfo(FluidResource resource, long amount, long capacity) {
		this.amount = amount;
		if (resource != null && !resource.isEmpty() && amount > 0) {
			translationKey = resource.getFluidType().getDescriptionId();
			fluidId = BuiltInRegistries.FLUID.getKey(resource.getFluid()).toString();
		}
		this.capacity = capacity;
	}

	public FluidInfo(Fluid fluid, long amount, long capacity) {
		if (fluid != null && !(fluid instanceof EmptyFluid)) {
			translationKey = fluid.getFluidType().getDescriptionId();
			fluidId = BuiltInRegistries.FLUID.getKey(fluid).toString();
		}
		this.amount = amount;
		this.capacity = capacity;
	}

	public void write(ICardReader reader) {
		if (translationKey != null)
			reader.setString("name", Language.getInstance().getOrDefault(translationKey));
		else
			reader.setString("name", "");
		if (fluidId != null)
			reader.setString("fluidName", fluidId);
		else
			reader.setString("fluidName", "");
		reader.setLong("amount", amount);
		reader.setLong("capacity", capacity);
	}

	public void write(ICardReader reader, int i) {
		if (translationKey != null)
			reader.setString(String.format("_%dname", i), Language.getInstance().getOrDefault(translationKey));
		else
			reader.setString(String.format("_%dname", i), "");
		reader.setLong(String.format("_%damount", i), amount);
		reader.setLong(String.format("_%dcapacity", i), capacity);
	}

	public static void addTank(String name, CompoundTag tag, FluidStack stack, int amount) {
		if (stack == null || stack.isEmpty())
			tag.putString(name, "N/A");
		else
			tag.putString(name, String.format("%s: %s mB", stack.getHoverName().getString(), amount));
	}
}
