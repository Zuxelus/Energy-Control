package com.zuxelus.energycontrol.utils;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.ICardReader;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public class FluidInfo {
	String fluidName;
	long amount;
	long capacity;
	int color;

	public FluidInfo(String fluidName, long amount, long capacity) {
		this.fluidName = fluidName;
		this.amount = amount;
		this.capacity = capacity;
	}

	public FluidInfo(IFluidTank tank) {
		if (tank.getFluid() != null) {
			amount = tank.getFluidAmount();
			if (amount > 0) {
				fluidName = FluidRegistry.getFluidName(tank.getFluid());
				color = tank.getFluid().getFluid().getColor();
			}
		}
		capacity = tank.getCapacity();
	}

	public FluidInfo(FluidStack stack, long capacity) {
		if (stack != null) {
			amount = stack.amount;
			if (amount > 0) {
				fluidName = FluidRegistry.getFluidName(stack.getFluid());
				color = stack.getFluid().getColor();
			}
		}
		this.capacity = capacity;
	}

	public FluidInfo(Fluid fluid, long amount, long capacity) {
		if (fluid != null) {
			fluidName = FluidRegistry.getFluidName(fluid);
			color = fluid.getColor();
		}
		this.amount = amount;
		this.capacity = capacity;
	}

	public void write(ICardReader reader) {
		Fluid fluid = FluidRegistry.getFluid(fluidName);
		if (fluid != null)
			reader.setString("name", fluid.getLocalizedName());
		else { // HBM 1.7.10
			String hbmName = "hbmfluid." + fluidName.toLowerCase();
			if (!StatCollector.translateToLocal(hbmName).equals(hbmName))
				reader.setString("name", StatCollector.translateToLocal(hbmName));
			else
				reader.setString("name", "");
		}
		reader.setString("fluidName", fluidName);
		reader.setLong("amount", amount);
		reader.setLong("capacity", capacity);
		reader.setInt("color", color);
	}

	public void write(ICardReader reader, int i) {
		Fluid fluid = FluidRegistry.getFluid(fluidName);
		if (fluid != null)
			reader.setString(String.format("_%dname", i), fluid.getLocalizedName());
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

	public static List<FluidInfo> toFluidInfoList(FluidTankInfo[] tanks) { // 1.7.10
		if (tanks == null || tanks.length == 0)
			return null;

		List<FluidInfo> result = new ArrayList<>();
		for (FluidTankInfo tank : tanks)
			result.add(new FluidInfo(tank.fluid, tank.capacity));
		return result;
	}
}
