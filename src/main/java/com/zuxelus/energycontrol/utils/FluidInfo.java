package com.zuxelus.energycontrol.utils;

import com.zuxelus.energycontrol.api.ICardReader;

import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidInfo {
	String translationKey;
	String fluidName;
	long amount;
	long capacity;

	public FluidInfo(String translationKey, String fluidName, long amount, long capacity) {
		this.translationKey = translationKey;
		this.fluidName = fluidName;
		this.amount = amount;
		this.capacity = capacity;
	}

	public FluidInfo(IFluidTank tank) {
		if (tank.getFluid() != null) {
			amount = tank.getFluidAmount();
			if (amount > 0) {
				translationKey = tank.getFluid().getTranslationKey();
				fluidName = ForgeRegistries.FLUIDS.getKey(tank.getFluid().getFluid()).toString();
			}
		}
		capacity = tank.getCapacity();
	}

	public FluidInfo(FluidStack stack, long capacity) {
		if (stack != null) {
			amount = stack.getAmount();
			if (amount > 0) {
				translationKey = stack.getTranslationKey();
				fluidName = ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString();
			}
		}
		this.capacity = capacity;
	}

	public FluidInfo(Fluid fluid, long amount, long capacity) {
		if (fluid != null && !(fluid instanceof EmptyFluid)) {
			translationKey = fluid.getFluidType().getDescriptionId();
			fluidName = ForgeRegistries.FLUIDS.getKey(fluid).toString();
		}
		this.amount = amount;
		this.capacity = capacity;
	}

	public void write(ICardReader reader) {
		if (translationKey != null)
			reader.setString("name", Language.getInstance().getOrDefault(translationKey));
		else
			reader.setString("name", "");
		if (fluidName != null)
			reader.setString("fluidName", fluidName);
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

	public static void addTank(String name, CompoundTag tag, IFluidTank tank) {
		FluidStack stack = tank.getFluid();
		if (stack == null)
			tag.putString(name, "N/A");
		else
			tag.putString(name, String.format("%s: %s mB", stack.getDisplayName().getString(), tank.getFluidAmount()));
	}

	public static void addTank(String name, CompoundTag tag, FluidStack stack, int amount) {
		if (stack == null)
			tag.putString(name, "N/A");
		else
			tag.putString(name, String.format("%s: %s mB", stack.getDisplayName().getString(), amount));
	}
}
