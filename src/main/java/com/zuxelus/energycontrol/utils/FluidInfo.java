package com.zuxelus.energycontrol.utils;

import com.zuxelus.energycontrol.api.ICardReader;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class FluidInfo {
	String name;
	String texture;
	long amount;
	long capacity;
	int color;

	public FluidInfo(String name, String texture, long amount, long capacity) {
		this.name = name;
		this.texture = texture;
		this.amount = amount;
		this.capacity = capacity;
	}

	public FluidInfo(IFluidTank tank) {
		if (tank.getFluid() != null) {
			amount = tank.getFluidAmount();
			if (amount > 0) {
				name = tank.getFluid().getLocalizedName();
				texture = tank.getFluid().getFluid().getStill().toString();
				color = tank.getFluid().getFluid().getColor();
			}
		}
		capacity = tank.getCapacity();
	}

	public FluidInfo(FluidStack stack, long capacity) {
		if (stack != null) {
			amount = stack.amount;
			if (amount > 0) {
				name = stack.getLocalizedName();
				texture = stack.getFluid().getStill().toString();
				color = stack.getFluid().getColor();
			}
		}
		this.capacity = capacity;
	}

	public FluidInfo(Fluid fluid, String name, long amount, long capacity) {
		if (fluid != null) {
			this.name = name;
			texture = fluid.getStill().toString();
			color = fluid.getColor();
		}
		this.amount = amount;
		this.capacity = capacity;
	}

	public void write(ICardReader reader) {
		if (name != null)
			reader.setString("name", name);
		else
			reader.setString("name", "");
		if (texture != null)
			reader.setString("texture", texture);
		else
			reader.setString("texture", "");
		reader.setLong("amount", amount);
		reader.setLong("capacity", capacity);
		reader.setInt("color", color);
	}

	public void write(ICardReader reader, int i) {
		if (name != null)
			reader.setString(String.format("_%dname", i), name);
		else
			reader.setString(String.format("_%dname", i), "");
		reader.setLong(String.format("_%damount", i), amount);
		reader.setLong(String.format("_%dcapacity", i), capacity);
	}

	public static void addTank(String name, NBTTagCompound tag, FluidTank tank) {
		FluidStack stack = tank.getFluid();
		if (stack == null)
			tag.setString(name, "N/A");
		else
			tag.setString(name, String.format("%s: %s mB", stack.getLocalizedName(), tank.getFluidAmount()));
	}

	public static void addTank(String name, NBTTagCompound tag, FluidStack stack) {
		if (stack == null)
			tag.setString(name, "N/A");
		else
			tag.setString(name, String.format("%s: %s mB", stack.getLocalizedName(), stack.amount));
	}
}
