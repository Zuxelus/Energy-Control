package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hbm.config.GeneralConfig;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.FT_Combustible;
import com.hbm.inventory.fluid.trait.FT_Coolable;
import com.hbm.inventory.recipes.MachineRecipes;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemRTGPellet;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.rbmk.RBMKDials;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBoiler;
import com.hbm.tileentity.machine.storage.*;
import com.hbm.util.RTGUtil;
import com.zuxelus.energycontrol.utils.DataHelper;
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

		ArrayList<Long> values = new ArrayList<>();
		FluidType in = te.tanks[0].getTankType();
		if (in.hasTrait(FT_Coolable.class)) {
			FT_Coolable trait = (FT_Coolable) in.getTrait(FT_Coolable.class);
			double eff = trait.getEfficiency(FT_Coolable.CoolingType.TURBINE);
			if (eff > 0.0D) {
				te.tanks[1].setTankType(trait.coolsTo);
				int inputOps = te.tanks[0].getFill() / trait.amountReq;
				int outputOps = (te.tanks[1].getMaxFill() - te.tanks[1].getFill()) / trait.amountProduced;
				int ops = Math.min(inputOps, outputOps);
				values.add((long) (ops * trait.amountReq));
				values.add((long) (ops * trait.amountProduced));
				values.add((long) (ops * trait.heatEnergy * eff));
			} else {
				values.add(0L);
				values.add(0L);
				values.add(0L);
			}
		} else {
			values.add(0L);
			values.add(0L);
			values.add(0L);
		}
		map.put(te, values);
	}

	@Hook
	public static void updateEntity(TileEntityCondenser te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		long convert = Math.min(te.tanks[0].getFill(), te.tanks[1].getMaxFill() - te.tanks[1].getFill());
		ArrayList<Long> values = new ArrayList<>();
		values.add(convert);
		map.put(te, values);
	}

	@Hook
	public static void updateEntity(TileEntityRBMKBoiler te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		long consumption = 0;
		long output = 0;
		double heatCap = te.getHeatFromSteam(te.steam.getTankType());
		double heatProvided = te.heat - heatCap;
		if (heatProvided > 0.0D && RBMKDials.getBoilerHeatConsumption(te.getWorldObj()) > 0) {
			int waterUsed = (int) Math.floor(heatProvided / RBMKDials.getBoilerHeatConsumption(te.getWorldObj()));
			consumption = Math.min(waterUsed, te.feed.getFill());
			if (te.getFactorFromSteam(te.steam.getTankType()) != 0)
				output = (int) Math.floor((waterUsed * 100) / te.getFactorFromSteam(te.steam.getTankType()));
		}
		ArrayList<Long> values = new ArrayList<>();
		values.add(consumption);
		values.add(output);
		map.put(te, values);
	}

	@Hook
	public static void updateEntity(TileEntityMachineTurbine te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		ArrayList<Long> values = new ArrayList<>();
		FluidType in = te.tanks[0].getTankType();
		if (in.hasTrait(FT_Coolable.class)) {
			FT_Coolable trait = (FT_Coolable) in.getTrait(FT_Coolable.class);
			double eff = trait.getEfficiency(FT_Coolable.CoolingType.TURBINE) * 0.85D;
			if (eff > 0.0D) {
				te.tanks[1].setTankType(trait.coolsTo);
				int inputOps = te.tanks[0].getFill() / trait.amountReq;
				int outputOps = (te.tanks[1].getMaxFill() - te.tanks[1].getFill()) / trait.amountProduced;
				int cap = 6000 / trait.amountReq;
				int ops = Math.min(inputOps, Math.min(outputOps, cap));
				values.add((long) (ops * trait.amountReq));
				values.add((long) (ops * trait.amountProduced));
				values.add((long) (ops * trait.heatEnergy * eff));
			} else {
				values.add(0L);
				values.add(0L);
				values.add(0L);
			}
		} else {
			values.add(0L);
			values.add(0L);
			values.add(0L);
		}
		map.put(te, values);
	}

	@Hook
	public static void updateEntity(TileEntityMachineLargeTurbine te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		ArrayList<Long> values = new ArrayList<>();
		FluidType in = te.tanks[0].getTankType();
		if (in.hasTrait(FT_Coolable.class)) {
			FT_Coolable trait = (FT_Coolable) in.getTrait(FT_Coolable.class);
			double eff = trait.getEfficiency(FT_Coolable.CoolingType.TURBINE);
			if (eff > 0.0D) {
				te.tanks[1].setTankType(trait.coolsTo);
				int inputOps = (int) Math.floor((te.tanks[0].getFill() / trait.amountReq));
				int outputOps = (te.tanks[1].getMaxFill() - te.tanks[1].getFill()) / trait.amountProduced;
				int cap = (int) Math.ceil(((te.tanks[0].getFill() / trait.amountReq) / 5.0F));
				int ops = Math.min(inputOps, Math.min(outputOps, cap));
				values.add((long) (ops * trait.amountReq));
				values.add((long) (ops * trait.amountProduced));
				values.add((long) (ops * trait.heatEnergy * eff));
			} else {
				values.add(0L);
				values.add(0L);
				values.add(0L);
			}
		} else {
			values.add(0L);
			values.add(0L);
			values.add(0L);
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

		boolean con = GeneralConfig.enableLBSM && GeneralConfig.enableLBSMIGen;
		int spin = 0;
		if (te.tanks[1].getFill() > 0)
			spin += te.getPowerFromFuel(con);

		for (int i = 0; i < 4; i++)
			if (te.burn[i] > 0)
				spin += con ? 75 : TileEntityMachineIGenerator.coalGenRate;

		int newHeat = 0;
		for (int slot : te.RTGSlots)
			if (te.slots[slot] != null && te.slots[slot].getItem() instanceof ItemRTGPellet)
				newHeat += RTGUtil.getPower((ItemRTGPellet) te.slots[slot].getItem(), te.slots[slot]);
		spin = (int) (spin + newHeat * (con ? 0.2D : TileEntityMachineIGenerator.rtgHeatMult));

		long powerGen = spin;
		if (spin > 0) {
			if (te.tanks[0].getFill() >= 10)
				powerGen += spin * TileEntityMachineIGenerator.waterPowerMult;
			if (te.tanks[2].getFill() >= 1)
				powerGen += spin * TileEntityMachineIGenerator.lubePowerMult;
			powerGen = (int) Math.pow(powerGen, TileEntityMachineIGenerator.heatExponent);
		}
		ArrayList<Long> values = new ArrayList<>();
		values.add(powerGen);
		map.put(te, values);
	}

	@Hook
	public static void burn(TileEntityCore te, long joules) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		long demand = (long) Math.ceil(joules / 1000D);
		if (te.tanks[0].getFill() < demand || te.tanks[1].getFill() < demand)
			return;

		ArrayList<Long> values = new ArrayList<>();
		values.add(demand);
		map.put(te, values);
	}

	@Hook(injectOnExit = true)
	public static void makePower(TileEntityMachineTurbineGas te, double consMax, int throttle) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		ArrayList<Double> values = new ArrayList<>();
		values.add(consMax * 0.05D + consMax * throttle / 100.0D);
		long energy = 0L;
		if (te.tanks[0].getTankType().hasTrait(FT_Combustible.class))
			energy = ((FT_Combustible) te.tanks[0].getTankType().getTrait(FT_Combustible.class)).getCombustionEnergy() / 1000L; 
		values.add(consMax * energy * (te.temp - te.tempIdle) / 220000.0D);
		//values.add(DataHelper.getDouble(TileEntityMachineTurbineGas.class, "waterPerTick", te));
		map.put(te, values);
	}
}