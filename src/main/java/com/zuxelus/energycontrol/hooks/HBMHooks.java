package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.MachineBoiler;
import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.inventory.MachineRecipes;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.rbmk.RBMKDials;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBoiler;
import com.zuxelus.hooklib.asm.Hook;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandlerModifiable;

public class HBMHooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();

	@Hook
	public static void update(TileEntityChungus te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		ArrayList<Integer> values = new ArrayList<>();
		Object[] outs = MachineRecipes.getTurbineOutput(te.types[0]);
		if (outs != null) {
			int processMax = (int) Math.ceil((te.tanks[0].getFluidAmount() / ((Integer) outs[2]).intValue()));
			int processSteam = te.tanks[0].getFluidAmount() / ((Integer) outs[2]).intValue();
			int processWater = (te.tanks[1].getCapacity() - te.tanks[1].getFluidAmount()) / ((Integer) outs[1]).intValue();
			int cycles = Math.min(processMax, Math.min(processSteam, processWater));

			values.add(((Integer) outs[2]).intValue() * cycles);
			values.add(((Integer) outs[1]).intValue() * cycles);
			values.add(((Integer) outs[3]).intValue() * cycles);
		} else {
			values.add(0);
			values.add(0);
			values.add(0);
		}
		map.put(te, values);
	}

	@Hook
	public static void update(TileEntityCondenser te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		int convert = Math.min(te.tanks[0].getFluidAmount(), te.tanks[1].getCapacity() - te.tanks[1].getFluidAmount());
		ArrayList<Integer> values = new ArrayList<>();
		values.add(convert);
		map.put(te, values);
	}

	@Hook
	public static void update(TileEntityRBMKBoiler te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		int consumption = 0;
		int output = 0;
		double heatCap = te.getHeatFromSteam(te.steamType);
		double heatProvided = te.heat - heatCap;
		if (heatProvided > 0.0D) {
			int waterUsed = (int) Math.floor(heatProvided / RBMKDials.getBoilerHeatConsumption(te.getWorld()));
			consumption = Math.min(waterUsed, te.feed.getFluidAmount());
			if (te.getFactorFromSteam(te.steamType) != 0)
			output = (int) Math.floor((waterUsed * 100) / te.getFactorFromSteam(te.steamType));
		}
		ArrayList<Integer> values = new ArrayList<>();
		values.add(consumption);
		values.add(output);
		map.put(te, values);
	}

	@Hook
	public static void update(TileEntityMachineTurbine te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		int cycles = 0;
		Object[] outs = MachineRecipes.getTurbineOutput((te.tanks[0].getFluid() == null) ? null : te.tanks[0].getFluid().getFluid());
		if (outs != null) {
			int processMax = 1200;
			int processSteam = te.tanks[0].getFluidAmount() / ((Integer) outs[2]).intValue();
			int processWater = (te.tanks[1].getCapacity() - te.tanks[1].getFluidAmount()) / ((Integer) outs[1]).intValue();
			cycles = Math.min(processMax, Math.min(processSteam, processWater));
		}

		ArrayList<Integer> values = map.get(te);
		if (values != null && values.size() > 0) {
			for (int i = 20; i > 0; i--)
				values.set(i, values.get(i - 1));
			values.set(0, cycles);
		} else {
			values = new ArrayList<>();
			for (int i = 0; i < 21; i++)
				values.add(cycles);
			map.put(te, values);
		}
	}

	@Hook
	public static void update(TileEntityMachineLargeTurbine te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		ArrayList<Integer> values = new ArrayList<>();
		Object[] outs = MachineRecipes.getTurbineOutput(te.types[0]);
		if (outs != null) {
			int processMax = (int) Math.ceil(Math.ceil((te.tanks[0].getFluidAmount() / 10.0F)) / ((Integer) outs[2]).intValue());
			int processSteam = te.tanks[0].getFluidAmount() / ((Integer) outs[2]).intValue();
			int processWater = (te.tanks[1].getCapacity() - te.tanks[1].getFluidAmount()) / ((Integer) outs[1]).intValue();
			int cycles = Math.min(processMax, Math.min(processSteam, processWater));
			values.add(((Integer) outs[2]).intValue() * cycles);
			values.add(((Integer) outs[1]).intValue() * cycles);
			values.add(((Integer) outs[3]).intValue() * cycles);
		} else {
			values.add(0);
			values.add(0);
			values.add(0);
		}
		map.put(te, values);
	}

	@Hook
	public static void update(TileEntityMachineBattery te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		ArrayList<Long> values = map.get(te);
		if (values != null && values.size() > 0) {
			for (int i = 20; i > 0; i--)
				values.set(i, values.get(i - 1));
			values.set(0, te.power);
		} else {
			values = new ArrayList<>();
			for (int i = 0; i < 21; i++)
				values.add(te.power);
			map.put(te, values);
		}
	}

	@Hook
	public static void update(TileEntitySolarBoiler te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		int water = 0;
		int steam = 0;
		IFluidTankProperties[] tanks = te.getTankProperties();
		FluidStack stack = tanks[0].getContents();
		if (stack != null)
			water = stack.amount;
		stack = tanks[1].getContents();
		if (stack != null)
			steam = stack.amount;

		int process = te.heat / 10;
		process = Math.min(process, water);
		process = Math.min(process, (tanks[1].getCapacity() - steam) / 100);
		ArrayList<Integer> values = new ArrayList<>();
		values.add(te.heat);
		values.add(process);
		map.put(te, values);
	}

	@Hook
	public static void update(TileEntityMachineBoilerElectric te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		Object[] outs = te.tanks[0].getFluid() != null ? MachineRecipes.getBoilerOutput(te.tanks[0].getFluid().getFluid()) : null;
		int heat = te.heat;

		if(heat > 2000)
			heat -= 30;

		if(te.power > 0)
			heat += Math.min(((double) te.power / (double) te.maxPower * 300), 150);
		else
			heat -= 100;

		if(heat > te.maxHeat)
			heat = te.maxHeat;

		int consumption = 0;
		int output = 0;
		if(outs != null)
			for(int i = 0; i < (heat / ((Integer) outs[3]).intValue()); i++)
				if(te.tanks[0].getFluidAmount() >= ((Integer) outs[2]).intValue() && te.tanks[1].getFluidAmount() + ((Integer) outs[1]).intValue() <= te.tanks[1].getCapacity()) {
					consumption += (Integer) outs[2];
					output += (Integer) outs[1];
					if(i == 0)
						heat -= 35;
					else
						heat -= 50;
				}

		ArrayList<Integer> values = new ArrayList<>();
		values.add(consumption);
		values.add(output);
		map.put(te, values);
	}
}