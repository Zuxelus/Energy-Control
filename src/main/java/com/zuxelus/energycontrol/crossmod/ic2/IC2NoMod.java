package com.zuxelus.energycontrol.crossmod.ic2;

import com.zuxelus.energycontrol.crossmod.EnergyStorageData;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class IC2NoMod extends IC2Cross {

	@Override
	public int getNuclearCellTimeLeft(ItemStack par1) {
		return 0;
	}

	@Override
	public boolean isWrench(ItemStack par1) {
		return false;
	}

	@Override
	public EnergyStorageData getEnergyStorageData(TileEntity target) {
		return null;
	}

	@Override
	public IC2Type getType() {
		return IC2Type.NONE;
	}

	@Override
	public boolean isSteamReactor(TileEntity par1) {
		return false;
	}

	@Override
	public ReactorInfo getReactorInfo(TileEntity par1) {
		return null;
	}

	@Override
	public boolean isMultiReactorPart(TileEntity par1) {
		return false;
	}

	@Override
	public EnergyStorageData getGeneratorData(TileEntity entity) {
		return null;
	}

}
