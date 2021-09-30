package com.zuxelus.energycontrol.utils;

import com.zuxelus.energycontrol.api.ICardReader;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class FluidInfo {
	String translationKey;
	String texture;
	long amount;
	long capacity;
	int color;

	public FluidInfo(String translationKey, String texture, long amount, long capacity) {
		this.translationKey = translationKey;
		this.texture = texture;
		this.amount = amount;
		this.capacity = capacity;
	}

	public FluidInfo(IFluidTank tank) {
		if (tank.getFluid() != null) {
			amount = tank.getFluidAmount();
			if (amount > 0) {
				translationKey = tank.getFluid().getUnlocalizedName();
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
				translationKey = stack.getUnlocalizedName();
				texture = stack.getFluid().getStill().toString();
				color = stack.getFluid().getColor();
			}
		}
		this.capacity = capacity;
	}

	public FluidInfo(Fluid fluid, long amount, long capacity) {
		if (fluid != null) {
			translationKey = fluid.getUnlocalizedName();
			texture = fluid.getStill().toString();
			color = fluid.getColor();
		}
		this.amount = amount;
		this.capacity = capacity;
	}

	public void write(ICardReader reader) {
		if (translationKey != null)
			reader.setString("name", I18n.translateToLocal(translationKey));
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
		if (translationKey != null)
			reader.setString(String.format("_%dname", i), I18n.translateToLocal(translationKey));
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
}
