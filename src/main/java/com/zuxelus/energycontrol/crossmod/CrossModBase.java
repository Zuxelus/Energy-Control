package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CrossModBase {

	public ItemStack getItemStack(String name) {
		return null;
	}

	public boolean isWrench(ItemStack stack) {
		return false;
	}

	public boolean isElectricItem(ItemStack stack) {
		return false;
	}

	public double dischargeItem(ItemStack stack, double needed) {
		return 0;
	}

	public NBTTagCompound getEnergyData(TileEntity te) {
		return null;
	}

	public NBTTagCompound getCardData(TileEntity te) {
		return null;
	}

	public NBTTagCompound getInventoryData(TileEntity te) {
		return null;
	}

	public int getHeat(World world, int x, int y, int z) {
		return -1;
	}

	public List<FluidInfo> getAllTanks(TileEntity te) {
		return null;
	}

	public ArrayList getHookValues(TileEntity te) {
		return null;
	}

	public void registerItems() { }

	public void loadRecipes() { }
}
