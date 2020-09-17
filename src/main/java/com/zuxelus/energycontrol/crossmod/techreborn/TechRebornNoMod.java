package com.zuxelus.energycontrol.crossmod.techreborn;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;

public class TechRebornNoMod extends CrossTechReborn {

	@Override
	public ItemStack getEnergyCard(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		return null;
	}

	@Override
	public ItemStack getGeneratorCard(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		return null;
	}

	public FluidTankInfo[] getAllTanks(TileEntity te) {
		return null;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		return null;
	}

	@Override
	public ItemStack getItemStack(String name) {
		return null;
	}
}
