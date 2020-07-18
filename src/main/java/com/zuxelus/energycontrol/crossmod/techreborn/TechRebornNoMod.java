package com.zuxelus.energycontrol.crossmod.techreborn;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class TechRebornNoMod extends CrossTechReborn {

	@Override
	public ItemStack getEnergyCard(World world, BlockPos pos) {
		return null;
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		return null;
	}

	@Override
	public ItemStack getGeneratorCard(World world, BlockPos pos) {
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		return null;
	}

	public IFluidTankProperties[] getAllTanks(TileEntity te) {
		return null;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		return null;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack) {
		return null;
	}

	@Override
	public ItemStack getItemStack(String name) {
		return null;
	}
}
