package com.zuxelus.energycontrol.utils;

import com.zuxelus.energycontrol.api.ICardReader;

import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;

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
				translationKey = tank.getFluid().getTranslationKey();
				texture = tank.getFluid().getFluid().getAttributes().getStillTexture().toString();
				color = tank.getFluid().getFluid().getAttributes().getColor();
			}
		}
		capacity = tank.getCapacity();
	}

	public FluidInfo(FluidStack stack, long capacity) {
		if (stack != null) {
			amount = stack.getAmount();
			if (amount > 0) {
				translationKey = stack.getTranslationKey();
				texture = stack.getFluid().getAttributes().getStillTexture().toString();
				color = stack.getFluid().getAttributes().getColor();
			}
		}
		this.capacity = capacity;
	}

	public FluidInfo(Fluid fluid, long amount, long capacity) {
		if (fluid != null) {
			translationKey = fluid.getAttributes().getTranslationKey();
			texture = fluid.getAttributes().getStillTexture().toString();
			color = fluid.getAttributes().getColor();
		}
		this.amount = amount;
		this.capacity = capacity;
	}

	public void write(ICardReader reader) {
		if (translationKey != null)
			reader.setString("name", I18n.get(translationKey));
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
			reader.setString(String.format("_%dname", i), I18n.get(translationKey));
		else
			reader.setString(String.format("_%dname", i), "");
		reader.setLong(String.format("_%damount", i), amount);
		reader.setLong(String.format("_%dcapacity", i), capacity);
	}

	public static void addTank(String name, CompoundNBT tag, FluidTank tank) {
		FluidStack stack = tank.getFluid();
		if (stack == null)
			tag.putString(name, "N/A");
		else
			tag.putString(name, String.format("%s: %s mB", stack.getTranslationKey(), tank.getFluidAmount()));
	}
}
