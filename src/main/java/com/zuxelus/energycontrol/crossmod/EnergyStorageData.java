package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;

public class EnergyStorageData {
	public static final int TARGET_TYPE_UNKNOWN = -1;
	public static final int TARGET_TYPE_IC2 = 0;
	public static final int TARGET_TYPE_RF = 1;
	public static final int TARGET_TYPE_GT6 = 2;
	public static final int TARGET_TYPE_MEK = 3;

	public ArrayList<Double> values = new ArrayList<Double>();
	public int type;
	public int subtype;

}
