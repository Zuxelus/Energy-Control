package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.inventory.MachineRecipes;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.rbmk.RBMKDials;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBoiler;
import com.zuxelus.hooklib.asm.Hook;

import net.minecraft.tileentity.TileEntity;

public class HBMHooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();

	@Hook
	public static void update(TileEntityChungus te) {
		if (te.getWorld().isRemote)
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
		if (te.getWorld().isRemote)
			return;

		int convert = Math.min(te.tanks[0].getFluidAmount(), te.tanks[1].getCapacity() - te.tanks[1].getFluidAmount());
		ArrayList<Integer> values = new ArrayList<>();
		values.add(convert);
		map.put(te, values);
	}

	@Hook
	public static void update(TileEntityRBMKBoiler te) {
		if (te.getWorld().isRemote)
			return;

		int consumption = 0;
		int output = 0;
		double heatCap = te.getHeatFromSteam(te.steamType);
		double heatProvided = te.heat - heatCap;
		if (heatProvided > 0.0D) {
			int waterUsed = (int) Math.floor(heatProvided / RBMKDials.getBoilerHeatConsumption(te.getWorld()));
			consumption = Math.min(waterUsed, te.feed.getFluidAmount());
			output = (int) Math.floor((waterUsed * 100) / te.getFactorFromSteam(te.steamType));
		}
		ArrayList<Integer> values = new ArrayList<>();
		values.add(consumption);
		values.add(output);
		map.put(te, values);
	}

	@Hook
	public static void update(TileEntityMachineTurbine te) {
		if (te.getWorld().isRemote)
			return;

		ArrayList<Integer> values = new ArrayList<>();
		Object[] outs = MachineRecipes.getTurbineOutput((te.tanks[0].getFluid() == null) ? null : te.tanks[0].getFluid().getFluid());
		if (outs != null) {
			int processMax = 1200;
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
	public static void update(TileEntityMachineLargeTurbine te) {
		if (te.getWorld().isRemote)
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
}