package com.zuxelus.energycontrol.crossmod;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.ModList;

public class CrossModLoader {
	public static CrossMod mekanism;

	public static void init() {
		mekanism = findMod("mekanism", "CrossModMekanism");
	}

	public static CrossMod findMod(String modId, String mainClass) {
		try {
			if (ModList.get().isLoaded(modId)) {
				Class<?> clz = Class.forName("com.zuxelus.energycontrol.crossmod." + mainClass);
				if (clz != null)
					return (CrossMod) clz.newInstance();
			}
		} catch (Exception e) { }
		return new CrossModEmpty();
	}

	public static ItemStack getEnergyCard(World world, BlockPos pos) {
		return mekanism.getEnergyCard(world, pos);
	}

	public static CompoundNBT getEnergyData(TileEntity te) {
		return mekanism.getEnergyData(te);
	}
	
	public static List<IFluidTank> getAllTanks(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null)
			return null;
		return mekanism.getAllTanks(te);
	}
}
