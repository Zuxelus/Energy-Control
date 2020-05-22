package com.zuxelus.energycontrol.crossmod.ic2;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;

import ic2.api.reactor.IReactor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.FluidTankProperties;

public class IC2NoMod extends IC2Cross {

	@Override
	public int getNuclearCellTimeLeft(ItemStack stack) {
		return 0;
	}

	@Override
	public boolean isWrench(ItemStack stack) {
		return false;
	}

	@Override
	public IC2Type getType() {
		return IC2Type.NONE;
	}

	@Override
	public ItemStack getItem(String name) {
		return null;
	}

	@Override
	public boolean isSteamReactor(TileEntity te) {
		return false;
	}

	@Override
	public boolean isCable(TileEntity te) {
		return false;
	}

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

	@Override
	public NBTTagCompound getGeneratorKineticData(TileEntity te) {
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorHeatData(TileEntity te) {
		return null;
	}

	@Override
	public FluidTankProperties[] getAllTanks(TileEntity te) {
		return null;
	}

	@Override
	public ItemStack getReactorCard(World world, BlockPos pos) {
		return null;
	}

	@Override
	public ItemStack getLiquidAdvancedCard(World world, BlockPos pos) {
		return null;
	}

	@Override
	public CardState updateCardReactor(World world, ICardReader reader, IReactor reactor) {
		return CardState.NO_TARGET;
	}

	@Override
	public CardState updateCardReactor5x5(World world, ICardReader reader, BlockPos target) {
		return CardState.NO_TARGET;
	}

}
