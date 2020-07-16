package com.zuxelus.energycontrol.crossmod.techreborn;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.Loader;

public abstract class CrossTechReborn {

	public static CrossTechReborn getMod() {
		try {
			if (Loader.isModLoaded("techreborn")) {
				Class clz = Class.forName("com.zuxelus.energycontrol.crossmod.techreborn.TechReborn");
				if (clz != null)
					return (CrossTechReborn) clz.newInstance();
			}
		} catch (Exception e) { }
		return new TechRebornNoMod();
	}

	public abstract ItemStack getEnergyCard(World world, BlockPos pos);

	public abstract NBTTagCompound getEnergyData(TileEntity te);

	public abstract ItemStack getGeneratorCard(World world, BlockPos pos);

	public abstract NBTTagCompound getGeneratorData(TileEntity te);

	public abstract List<IFluidTank> getAllTanks(TileEntity te);

	public abstract ItemStack getChargedStack(ItemStack stack);

	public abstract ICapabilityProvider initCapabilities(ItemStack stack);
	
	public abstract ItemStack getItemStack(String name);
}
