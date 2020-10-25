package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.OreHelper;

import nc.config.NCConfig;
import nc.init.NCBlocks;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CrossNuclearCraft extends CrossModBase {

	public CrossNuclearCraft() {
		super("nuclearcraft", null, null);
	}

	public void loadOreInfo() {
		for (int i = 0; i < 8; i++)
			if (NCConfig.ore_gen[i])
				EnergyControl.oreHelper.put(OreHelper.getId(NCBlocks.ore, i), new OreHelper(NCConfig.ore_min_height[i], NCConfig.ore_max_height[i],  NCConfig.ore_size[i] + 2, NCConfig.ore_rate[i]));
	}
}
