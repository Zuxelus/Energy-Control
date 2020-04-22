package com.zuxelus.energycontrol.crossmod.ic2;

import com.zuxelus.energycontrol.crossmod.EnergyStorageData;

import net.minecraftforge.fml.common.Loader;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class IC2Cross {
	public enum IC2Type{
		SPEIGER,
		EXP,
		NONE
	}

	public abstract boolean isWrench(ItemStack par1);

	public abstract int getNuclearCellTimeLeft(ItemStack par1);

	public abstract boolean isSteamReactor(TileEntity par1);

	//public abstract EnergyStorageData getStorageData(TileEntity target);

	// Is not a IReactor because i have no clue if you implement the IC2 API or
	// not...
	// If you have no problems with using IReactor here and at isSteamReactor
	// then change TileEntity back to IReactor
	public abstract ReactorInfo getReactorInfo(TileEntity par1);

	public abstract boolean isMultiReactorPart(TileEntity par1);

	public abstract IC2Type getType();

	public static class ReactorInfo {
		public boolean isOnline;
		public int outTank;
		public int inTank;
		public int emitHeat;
		public int coreTemp;
	}

	public static IC2Cross getIC2Cross() {
		try {
			if (Loader.isModLoaded("IC2-Classic-Spmod")) {
				Class clz = Class.forName("com.zuxelus.energycontrol.crossmod.ic2.IC2ClassicCross");
				if (clz != null)
					return (IC2Cross) clz.newInstance();
			} 
			if (Loader.isModLoaded("IC2")) {
				Class clz = Class.forName("com.zuxelus.energycontrol.crossmod.ic2.IC2ExpCross");
				if (clz != null)
					return (IC2Cross) clz.newInstance();
			}
		} catch (Exception e) {

		}
		return new IC2NoMod();
	}

	public abstract EnergyStorageData getEnergyStorageData(TileEntity entity);
	
	public abstract NBTTagCompound getGeneratorData(TileEntity entity);
}
