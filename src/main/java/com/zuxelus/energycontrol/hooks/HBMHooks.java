package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.MachineRecipes;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemRTGPellet;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.rbmk.RBMKDials;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBoiler;
import com.hbm.tileentity.machine.storage.*;
import com.hbm.util.RTGUtil;
import com.zuxelus.hooklib.asm.Hook;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

public class HBMHooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();

	@Hook
	public static void updateEntity(TileEntityChungus te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
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
	public static void updateEntity(TileEntityCondenser te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		int convert = Math.min(te.tanks[0].getFill(), te.tanks[1].getMaxFill() - te.tanks[1].getFill());
		ArrayList<Integer> values = new ArrayList<>();
		values.add(convert);
		map.put(te, values);
	}

	@Hook
	public static void updateEntity(TileEntityRBMKBoiler te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		int consumption = 0;
		int output = 0;
		double heatCap = te.getHeatFromSteam(te.steam.getTankType());
		double heatProvided = te.heat - heatCap;
		if (heatProvided > 0.0D && RBMKDials.getBoilerHeatConsumption(te.getWorldObj()) > 0) {
			int waterUsed = (int) Math.floor(heatProvided / RBMKDials.getBoilerHeatConsumption(te.getWorldObj()));
			consumption = Math.min(waterUsed, te.feed.getFill());
			if (te.getFactorFromSteam(te.steam.getTankType()) != 0)
				output = (int) Math.floor((waterUsed * 100) / te.getFactorFromSteam(te.steam.getTankType()));
		}
		ArrayList<Integer> values = new ArrayList<>();
		values.add(consumption);
		values.add(output);
		map.put(te, values);
	}

	@Hook
	public static void updateEntity(TileEntityMachineTurbine te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
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
	public static void updateEntity(TileEntityMachineLargeTurbine te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
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

	@Hook
	public static void updateEntity(TileEntityMachineBattery te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
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
	public static void updateEntity(TileEntityMachineFENSU te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
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
	public static void updateEntity(TileEntityMachineIGenerator te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		int spin = 0;
		if (te.tanks[1].getFill() > 0)
			spin += te.getPowerFromFuel();

		for (int i = 0; i < 4; i++)
			if (te.burn[i] > 0)
				spin += 75;

		int newHeat = 0;
		for (int slot : te.RTGSlots)
			if (te.slots[slot] != null && te.slots[slot].getItem() instanceof ItemRTGPellet)
				newHeat += RTGUtil.getPower((ItemRTGPellet) te.slots[slot].getItem(), te.slots[slot]);
		spin = (int) (spin + newHeat * 0.2D);

		int powerGen = spin;
		if (spin > 0) {
			if (te.tanks[0].getFill() >= 10)
				powerGen += spin;
			if (te.tanks[2].getFill() >= 1)
				powerGen += spin * 3;
			powerGen = (int) Math.pow(powerGen, 1.1D);
		}
		ArrayList<Integer> values = new ArrayList<>();
		values.add(powerGen);
		map.put(te, values);
	}

	@Hook
	public static void burn(TileEntityCore te, long joules) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		int demand = (int) Math.ceil(joules / 1000D);
		if (te.tanks[0].getFill() < demand || te.tanks[1].getFill() < demand)
			return;

		ArrayList<Integer> values = new ArrayList<>();
		values.add(demand);
		map.put(te, values);
	}
}