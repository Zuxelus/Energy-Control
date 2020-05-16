package com.zuxelus.energycontrol.crossmod.ic2;

import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.crossmod.EnergyStorageData;

import ic2.api.reactor.IReactor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.Loader;

public abstract class IC2Cross {
	public enum IC2Type{
		SPEIGER,
		EXP,
		NONE
	}

	public abstract boolean isWrench(ItemStack stack);

	public abstract int getNuclearCellTimeLeft(ItemStack stack);

	public abstract boolean isSteamReactor(TileEntity te);
	
	public abstract boolean isCable(TileEntity te);

	public abstract IC2Type getType();
	
	public abstract int getProfile();

	/*public static class ReactorInfo {
		public boolean isOnline;
		public int outTank;
		public int inTank;
		public int emitHeat;
		public int coreTemp;
	}*/

	public static IC2Cross getIC2Cross() {
		try {
			if (Loader.isModLoaded("ic2-classic-spmod")) {
				Class clz = Class.forName("com.zuxelus.energycontrol.crossmod.ic2.IC2ClassicCross");
				if (clz != null)
					return (IC2Cross) clz.newInstance();
			} 
			if (Loader.isModLoaded("ic2")) {
				Class clz = Class.forName("com.zuxelus.energycontrol.crossmod.ic2.IC2ExpCross");
				if (clz != null)
					return (IC2Cross) clz.newInstance();
			}
		} catch (Exception e) {

		}
		return new IC2NoMod();
	}

	public abstract EnergyStorageData getEnergyStorageData(TileEntity entity);
	
	public abstract ItemStack getGeneratorCard(World world, BlockPos pos);
	public abstract NBTTagCompound getGeneratorData(TileEntity entity);
	
	public abstract NBTTagCompound getGeneratorKineticData(TileEntity entity);

	public abstract NBTTagCompound getGeneratorHeatData(TileEntity entity);
	
	public abstract List<IFluidTank> getAllTanks(TileEntity te);
	
	public abstract ItemStack getReactorCard(World world, BlockPos pos);
	
	public abstract ItemStack getLiquidAdvancedCard(World world, BlockPos pos);
	
	public abstract CardState updateCardReactor(World world, ICardReader reader, IReactor reactor);
	public abstract CardState updateCardReactor5x5(World world, ICardReader reader, BlockPos target);
}
