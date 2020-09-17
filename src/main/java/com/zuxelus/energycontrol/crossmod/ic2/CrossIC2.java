package com.zuxelus.energycontrol.crossmod.ic2;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;

import cpw.mods.fml.common.Loader;
import ic2.api.reactor.IReactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;

public abstract class CrossIC2 {
	public enum IC2Type{
		SPEIGER,
		EXP,
		NONE
	}

	public abstract boolean isWrench(ItemStack stack);

	public abstract int getNuclearCellTimeLeft(ItemStack stack);

	public abstract boolean isSteamReactor(TileEntity te);

	public abstract IC2Type getType();

	public abstract ItemStack getItemStack(String name);

	public abstract Item getItem(String name);

	public abstract ItemStack getChargedStack(ItemStack stack);

	public static CrossIC2 getMod() {
		try {
			if (Loader.isModLoaded("IC2-Classic-Spmod")) {
				Class clz = Class.forName("com.zuxelus.energycontrol.crossmod.ic2.IC2Classic");
				if (clz != null)
					return (CrossIC2) clz.newInstance();
			}
			if (Loader.isModLoaded("IC2")) {
				Class clz = Class.forName("com.zuxelus.energycontrol.crossmod.ic2.IC2Exp");
				if (clz != null)
					return (CrossIC2) clz.newInstance();
			}
		} catch (Exception e) {

		}
		return new IC2NoMod();
	}

	public abstract ItemStack getEnergyCard(World world, int x, int y, int z);

	public abstract NBTTagCompound getEnergyData(TileEntity te);

	public abstract ItemStack getGeneratorCard(World world, int x, int y, int z);

	public abstract NBTTagCompound getGeneratorData(TileEntity te);
	
	public abstract NBTTagCompound getGeneratorKineticData(TileEntity te);

	public abstract NBTTagCompound getGeneratorHeatData(TileEntity te);
	
	public abstract FluidTankInfo[] getAllTanks(TileEntity te);
	
	public abstract ItemStack getReactorCard(World world, int x, int y, int z);
	
	public abstract ItemStack getLiquidAdvancedCard(World world, int x, int y, int z);
	
	public abstract CardState updateCardReactor(World world, ICardReader reader, IReactor reactor);
	public abstract CardState updateCardReactor5x5(World world, ICardReader reader, int x, int y, int z);
	
	public abstract void showBarrelInfo(EntityPlayer player, TileEntity te);
}
