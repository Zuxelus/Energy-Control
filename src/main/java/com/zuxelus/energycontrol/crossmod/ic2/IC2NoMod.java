package com.zuxelus.energycontrol.crossmod.ic2;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;

import ic2.api.reactor.IReactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;

public class IC2NoMod extends CrossIC2 {

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
	public ItemStack getItemStack(String name) {
		return null;
	}

	@Override
	public Item getItem(String name) {
		return null;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		return null;
	}

	@Override
	public boolean isSteamReactor(TileEntity te) {
		return false;
	}

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

	@Override
	public NBTTagCompound getGeneratorKineticData(TileEntity te) {
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorHeatData(TileEntity te) {
		return null;
	}

	@Override
	public FluidTankInfo[] getAllTanks(TileEntity te) {
		return null;
	}

	@Override
	public ItemStack getReactorCard(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public ItemStack getLiquidAdvancedCard(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public CardState updateCardReactor(World world, ICardReader reader, IReactor reactor) {
		return CardState.NO_TARGET;
	}

	@Override
	public CardState updateCardReactor5x5(World world, ICardReader reader, int x, int y, int z) {
		return CardState.NO_TARGET;
	}

	@Override
	public void showBarrelInfo(EntityPlayer player, TileEntity te) { }
}
