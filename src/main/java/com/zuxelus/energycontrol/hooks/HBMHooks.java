package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.MachineRecipes;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.rbmk.RBMKDials;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBoiler;
import com.zuxelus.hooklib.asm.Hook;

import net.minecraft.tileentity.TileEntity;

public class HBMHooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();

	@Hook
	public static void update(TileEntityChungus te) {
		if (te.getWorldObj().isRemote)
			return;

		ArrayList<Integer> values = new ArrayList<>();
		Object[] outs = MachineRecipes.getTurbineOutput(te.tanks[0].getTankType());
		if (outs == null) {
			te.tanks[0].setTankType(Fluids.STEAM);
			te.tanks[1].setTankType(Fluids.SPENTSTEAM);
			outs = MachineRecipes.getTurbineOutput(te.tanks[0].getTankType());
		}
		if (outs != null) {
			int processMax = (int) Math.ceil((te.tanks[0].getFill() / ((Integer) outs[2]).intValue()));
			int processSteam = te.tanks[0].getFill() / ((Integer) outs[2]).intValue();
			int processWater = (te.tanks[1].getMaxFill() - te.tanks[1].getFill()) / ((Integer) outs[1]).intValue();
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
		if (te.getWorldObj().isRemote)
			return;

		int convert = Math.min(te.tanks[0].getFill(), te.tanks[1].getMaxFill() - te.tanks[1].getFill());
		ArrayList<Integer> values = new ArrayList<>();
		values.add(convert);
		map.put(te, values);
	}

	@Hook
	public static void update(TileEntityRBMKBoiler te) {
		if (te.getWorldObj().isRemote)
			return;

		int consumption = 0;
		int output = 0;
		double heatCap = te.getHeatFromSteam(te.steam.getTankType());
		double heatProvided = te.heat - heatCap;
		if (heatProvided > 0.0D) {
			int waterUsed = (int) Math.floor(heatProvided / RBMKDials.getBoilerHeatConsumption(te.getWorldObj()));
			consumption = Math.min(waterUsed, te.feed.getFill());
			output = (int) Math.floor((waterUsed * 100) / te.getFactorFromSteam(te.steam.getTankType()));
		}
		ArrayList<Integer> values = new ArrayList<>();
		values.add(consumption);
		values.add(output);
		map.put(te, values);
	}

	@Hook
	public static void update(TileEntityMachineTurbine te) {
		if (te.getWorldObj().isRemote)
			return;

		ArrayList<Integer> values = new ArrayList<>();
		Object[] outs = MachineRecipes.getTurbineOutput(te.tanks[0].getTankType());
		if (outs != null) {
			int processMax = 1200;
			int processSteam = te.tanks[0].getFill() / ((Integer) outs[2]).intValue();
			int processWater = (te.tanks[1].getMaxFill() - te.tanks[1].getFill()) / ((Integer) outs[1]).intValue();
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
		if (te.getWorldObj().isRemote)
			return;

		ArrayList<Integer> values = new ArrayList<>();
		Object[] outs = MachineRecipes.getTurbineOutput(te.tanks[0].getTankType());
		if (outs != null) {
			int processMax = (int) Math.ceil(Math.ceil((te.tanks[0].getFill() / 5.0F)) / ((Integer) outs[2]).intValue());
			int processSteam = te.tanks[0].getFill() / ((Integer) outs[2]).intValue();
			int processWater = (te.tanks[1].getMaxFill() - te.tanks[1].getFill()) / ((Integer) outs[1]).intValue();
			int cycles = Math.min(processMax, Math.min(processSteam, processWater));
			values.add(((Integer) outs[2]).intValue() * cycles);
			values.add(((Integer) outs[1]).intValue() * cycles);
			values.add((int) (((Integer) outs[3]).intValue() * cycles * 1.25D));
		} else {
			values.add(0);
			values.add(0);
			values.add(0);
		}
		map.put(te, values);
	}
}