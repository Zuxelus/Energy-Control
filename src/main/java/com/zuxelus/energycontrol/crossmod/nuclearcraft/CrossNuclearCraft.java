package com.zuxelus.energycontrol.crossmod.nuclearcraft;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.Loader;

public abstract class CrossNuclearCraft {

	public static CrossNuclearCraft getMod() {
		try {
			if (Loader.isModLoaded("nuclearcraft")) {
				Class clz = Class.forName("com.zuxelus.energycontrol.crossmod.nuclearcraft.NuclearCraft");
				if (clz != null)
					return (CrossNuclearCraft) clz.newInstance();
			} 
		} catch (Exception e) { }
		return new NuclearCraftNoMod();
	}

	public abstract List<IFluidTank> getAllTanks(TileEntity te);

	public abstract int getReactorHeat(World world, BlockPos pos);

	public abstract void loadOreInfo();
}
