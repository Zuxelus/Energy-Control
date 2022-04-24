package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.utils.FluidInfo;

import ic2.api.reactor.IReactor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.IFluidTank;

import java.util.List;

public class CrossModBase {

	public Item getItem(String name) {
		return null;
	}

	public ItemStack getItemStack(String name) {
		return null;
	}

	public ItemStack getChargedStack(ItemStack stack) {
		return null;
	}

	public ICapabilityProvider initCapabilities(ItemStack stack) {
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

	//public void postModEvent(TileEntity te, String name) {}

	public int getNuclearCellTimeLeft(ItemStack stack) {
		return 0;
	}

	public NBTTagCompound getEnergyData(TileEntity te) {
		return null;
	}

	public ItemStack getGeneratorCard(TileEntity te) {
		return null;
	}

	public NBTTagCompound getGeneratorData(TileEntity te) {
		return null;
	}

	public NBTTagCompound getGeneratorHeatData(TileEntity te) {
		return null;
	}

	public NBTTagCompound getGeneratorKineticData(TileEntity te) {
		return null;
	}

	public ItemStack getReactorCard(World world, BlockPos pos) {
		return null;
	}

	public NBTTagCompound getCardData(TileEntity te) {
		return null;
	}

	public NBTTagCompound getInventoryData(TileEntity te) {
		return null;
	}

	public NBTTagCompound getReactorData(TileEntity te) {
		return null;
	}

	public NBTTagCompound getReactor5x5Data(TileEntity te) {
		return null;
	}

	public int getReactorHeat(World world, BlockPos pos) {
		return -1;
	}

	public boolean isSteamReactor(TileEntity te) {
		return false;
	}

	public List<FluidInfo> getAllTanks(TileEntity te) {
		return null;
	}

	public void registerItems(boolean isClient) { } // 1.10.2 and less

	public void loadRecipes() { } // 1.10.2 and less
}
