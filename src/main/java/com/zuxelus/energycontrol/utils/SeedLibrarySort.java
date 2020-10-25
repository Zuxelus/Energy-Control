package com.zuxelus.energycontrol.utils;

import ic2.api.crops.ICropSeed;
import ic2.core.item.ItemCropSeed;
import net.minecraft.item.ItemStack;

public class SeedLibrarySort implements java.util.Comparator<ItemStack> {
	public static final int NO_SORT = -1;
	public static final int GROWTH = 0;
	public static final int GAIN = 1;
	public static final int RESISTANCE = 2;
	public static final int TOTAL = 3;

	public static final SeedLibrarySort NONE = new SeedLibrarySort(NO_SORT, false);
	public static final SeedLibrarySort GROWTH_ASC = new SeedLibrarySort(GROWTH, false);
	public static final SeedLibrarySort GROWTH_DESC = new SeedLibrarySort(GROWTH, true);
	public static final SeedLibrarySort GAIN_ASC = new SeedLibrarySort(GAIN, false);
	public static final SeedLibrarySort GAIN_DESC = new SeedLibrarySort(GAIN, true);
	public static final SeedLibrarySort RESISTANCE_ASC = new SeedLibrarySort(RESISTANCE, false);
	public static final SeedLibrarySort RESISTANCE_DESC = new SeedLibrarySort(RESISTANCE, true);
	public static final SeedLibrarySort TOTAL_ASC = new SeedLibrarySort(TOTAL, false);
	public static final SeedLibrarySort TOTAL_DESC = new SeedLibrarySort(TOTAL, true);

	public final int sort_type;
	public final boolean descending;

	public SeedLibrarySort(int type, boolean desc) {
		sort_type = type;
		descending = desc;
	}

	public static SeedLibrarySort getSort(int type, boolean desc) {
		if (type == NO_SORT)
			return NONE;

		if (type == GROWTH) {
			if (desc)
				return GROWTH_DESC;
			return GROWTH_ASC;
		} else if (type == GAIN) {
			if (desc)
				return GAIN_DESC;
			return GAIN_ASC;
		} else if (type == RESISTANCE) {
			if (desc)
				return RESISTANCE_DESC;
			return RESISTANCE_ASC;
		} else { // if (type == TOTAL) {
			if (desc)
				return TOTAL_DESC;
			return TOTAL_ASC;
		}
	}

	@Override
	public int compare(ItemStack seed_1, ItemStack seed_2) {
		if (sort_type == NO_SORT)
			return 0;

		// If we're sorting, sort less-scanned seeds to the back, sorted by
		// scan level only.
		ICropSeed seed = (ICropSeed) seed_1.getItem();
		int scan_1 = seed.getScannedFromStack(seed_1);
		int scan_2 = seed.getScannedFromStack(seed_2);
		if (scan_1 != scan_2)
			return scan_1 - scan_2;
		if (scan_1 < 4)
			return 0;

		int value_1, value_2;
		if (sort_type == GROWTH) {
			value_1 = seed.getGrowthFromStack(seed_1);
			value_2 = seed.getGrowthFromStack(seed_2);
		} else if (sort_type == GAIN) {
			value_1 = seed.getGainFromStack(seed_1);
			value_2 = seed.getGainFromStack(seed_2);
		} else if (sort_type == RESISTANCE) {
			value_1 = seed.getResistanceFromStack(seed_1);
			value_2 = seed.getResistanceFromStack(seed_2);
		} else { // if (sort_type == TOTAL) {
			value_1 = seed.getGrowthFromStack(seed_1) + seed.getGainFromStack(seed_1) + seed.getResistanceFromStack(seed_1);
			value_2 = seed.getGrowthFromStack(seed_2) + seed.getGainFromStack(seed_2) + seed.getResistanceFromStack(seed_2);
		}

		if (descending)
			return value_1 - value_2;
		return value_2 - value_1;
	}
}
