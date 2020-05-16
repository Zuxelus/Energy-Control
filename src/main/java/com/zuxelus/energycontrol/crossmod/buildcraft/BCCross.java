package com.zuxelus.energycontrol.crossmod.buildcraft;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.Loader;

public abstract class BCCross {
	public static BCCross getBuildCraftCross() {
		try {
			if (Loader.isModLoaded("buildcraftcore")) {
				Class clz = Class.forName("com.zuxelus.energycontrol.crossmod.buildcraft.BuildCraftCross");
				if (clz != null)
					return (BCCross) clz.newInstance();
			} 
		} catch (Exception e) { }
		return new BuildCraftNoMod();
	}
	public abstract boolean modLoaded();
	
	public abstract List<IFluidTank> getAllTanks(TileEntity te);

	public abstract ItemStack getGeneratorCard(World world, BlockPos pos);

	public abstract NBTTagCompound getEngineData(TileEntity entity);
}
