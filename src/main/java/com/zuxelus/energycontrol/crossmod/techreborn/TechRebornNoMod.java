package com.zuxelus.energycontrol.crossmod.techreborn;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.IFluidTank;

public class TechRebornNoMod extends CrossTechReborn {

	@Override
	public ItemStack getEnergyCard(World world, BlockPos pos) {
		return ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		return null;
	}

	@Override
	public ItemStack getGeneratorCard(World world, BlockPos pos) {
		return ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		return null;
	}

	public List<IFluidTank> getAllTanks(TileEntity te) {
		return null;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		return ItemStack.EMPTY;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack) {
		return null;
	}

	@Override
	public ItemStack getItemStack(String name) {
		return ItemStack.EMPTY;
	}
}
