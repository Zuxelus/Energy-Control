package com.zuxelus.energycontrol.utils;

import java.lang.reflect.Field;

import ic2.core.block.generator.tileentity.TileEntityRTGenerator;
import net.minecraft.tileentity.TileEntity;

public class DataHelper {
	public static final String ACTIVE = "active";
	public static final String AMOUNT = "amount"; 
	public static final String CAPACITY = "capacity";
	public static final String CAPACITYHU = "capacityHU";
	public static final String CAPACITYKU = "capacityKU";
	public static final String CAPACITYTU = "capacityTU";
	public static final String CONSUMPTION = "consumption";
	public static final String CONSUMPTIONHE = "consumptionHE";
	public static final String CONSUMPTIONTU = "consumptionTU";
	public static final String DIFF = "diff";
	public static final String ENERGY = "energy";
	public static final String ENERGYHU = "energyHU";
	public static final String ENERGYKU = "energyKU";
	public static final String ENERGYTU = "energyTU";
	public static final String EUTYPE = "euType";
	public static final String FUEL = "fuel";
	public static final String HEAT = "heat";
	public static final String MAXHEAT = "maxHeat";
	public static final String MULTIPLIER = "multiplier";
	public static final String OUTPUT = "output";
	public static final String OUTPUTHU = "outputHU";
	public static final String OUTPUTKU = "outputKU";
	public static final String OUTPUTTU = "outputTU";
	public static final String OUTPUTMB = "outputmb";
	public static final String PRESSURE = "pressure";
	public static final String TANK = "tank";
	public static final String TANK2 = "tank2";
	public static final String TANK3 = "tank3";
	public static final String TANK4 = "tank4";
	public static final String TANK5 = "tank5";

	public static double getDouble(Class obj, String name, TileEntity te) {
		try {
			Field field = obj.getDeclaredField(name);
			field.setAccessible(true);
			return (Double) field.get(te);
		} catch (Throwable t) { }
		return 0.0D;
	}

	public static float getFloat(Class obj, String name, TileEntity te) {
		try {
			Field field = obj.getDeclaredField(name);
			field.setAccessible(true);
			return (Float) field.get(te);
		} catch (Throwable t) { }
		return 0.0F;
	}

	public static int getInt(Class obj, String name, TileEntity te) {
		try {
			Field field = obj.getDeclaredField(name);
			field.setAccessible(true);
			return (int) field.get(te);
		} catch (Throwable t) { }
		return 0;
	}

	public static short getShort(Class obj, String name, TileEntity te) {
		try {
			Field field = obj.getDeclaredField(name);
			field.setAccessible(true);
			return (Short) field.get(te);
		} catch (Throwable t) { }
		return 0;
	}
}

