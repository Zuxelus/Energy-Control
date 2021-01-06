package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;

import ic2.api.reactor.IReactor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;

public class CrossModBase {

	public String getModType() {
		return "";
	}

	public Item getItem(String name) {
		return null;
	}

	public ItemStack getItemStack(String name) {
		return null;
	}

	public ItemStack getChargedStack(ItemStack stack) {
		return null;
	}

	public boolean isWrench(ItemStack stack) {
		return false;
	}

	public int getNuclearCellTimeLeft(ItemStack stack) {
		return 0;
	}

	public NBTTagCompound getEnergyData(TileEntity te) {
		return null;
	}

	public ItemStack getGeneratorCard(World world, int x, int y, int z) {
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

	public ItemStack getReactorCard(World world, int x, int y, int z) {
		return null;
	}

	public int getReactorHeat(World world, int x, int y, int z) {
		return -1;
	}

	public CardState updateCardReactor(World world, ICardReader reader, IReactor reactor) {
		return CardState.NO_TARGET;
	}

	public CardState updateCardReactor5x5(World world, ICardReader reader, int x, int y, int z) {
		return CardState.NO_TARGET;
	}

	public FluidTankInfo[] getAllTanks(TileEntity te) {
		return null;
	}
}
