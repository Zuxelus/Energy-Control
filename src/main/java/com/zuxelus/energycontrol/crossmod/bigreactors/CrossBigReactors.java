package com.zuxelus.energycontrol.crossmod.bigreactors;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.Loader;

public abstract class CrossBigReactors {

	public static CrossBigReactors getMod() {
		try {
			if (Loader.isModLoaded("bigreactors")) {
				Class clz = Class.forName("com.zuxelus.energycontrol.crossmod.bigreactors.BigReactors");
				if (clz != null)
					return (CrossBigReactors) clz.newInstance();
			} 
		} catch (Exception e) { }
		return new BigReactorsNoMod();
	}

	public abstract List<IFluidTank> getAllTanks(TileEntity te);

	public abstract int getReactorHeat(World world, BlockPos pos);
}