package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
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

	public String getModType() {
		return "";
	}

	public int getProfile() {
		return -1;
	}

	public Item getItem(String name) {
		return null;
	}

	public ItemStack getItemStack(String name) {
		return ItemStack.EMPTY;
	}

	public ItemStack getChargedStack(ItemStack stack) {
		return ItemStack.EMPTY;
	}

	public ICapabilityProvider initCapabilities(ItemStack stack) {
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

	public ItemStack getGeneratorCard(TileEntity te) {
		return ItemStack.EMPTY;
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
		return ItemStack.EMPTY;
	}

	public int getReactorHeat(World world, BlockPos pos) {
		return -1;
	}

	public boolean isSteamReactor(TileEntity te) {
		return false;
	}

	public CardState updateCardReactor(World world, ICardReader reader, IReactor reactor) {
		return CardState.NO_TARGET;
	}

	public CardState updateCardReactor5x5(World world, ICardReader reader, BlockPos target) {
		return CardState.NO_TARGET;
	}

	public List<IFluidTank> getAllTanks(TileEntity te) {
		return null;
	}

	public void loadOreInfo() { }
}
