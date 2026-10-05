package com.zuxelus.energycontrol.utils;

import com.zuxelus.energycontrol.api.ICardReader;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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

	// amounts in mB, for tanks that are not exposed through the Transfer API (Tech Reborn on 1.16)
	public FluidInfo(Fluid fluid, long amount, long capacity) {
		this.amount = amount;
		if (amount > 0 && fluid != Fluids.EMPTY) {
			Identifier id = Registry.FLUID.getId(fluid);
			name = getName(fluid, id).getString();
			this.fluid = id.toString();
		}
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
			Identifier id = Registry.FLUID.getId(variant.getFluid());
			name = getName(variant.getFluid(), id).getString();
			fluid = id.toString();
		}
		capacity = stack.getCapacity() / 81;
	}

	// FluidVariantAttributes is not available in Fabric API for 1.16: use the fluid block name, as its default handler does
	private static Text getName(Fluid fluid, Identifier id) {
		Block block = fluid.getDefaultState().getBlockState().getBlock();
		if (block != Blocks.AIR)
			return block.getName();
		return new TranslatableText("block." + id.getNamespace() + "." + id.getPath());
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
