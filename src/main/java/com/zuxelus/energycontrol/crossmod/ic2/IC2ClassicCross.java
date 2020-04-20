package com.zuxelus.energycontrol.crossmod.ic2;

import com.zuxelus.energycontrol.crossmod.EnergyStorageData;

import ic2.api.reactor.IReactor;
import ic2.api.tile.IEnergyStorage;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class IC2ClassicCross extends IC2Cross {
	@Override
	public int getNuclearCellTimeLeft(ItemStack par1) {
		if (par1 == null)
			return 0;
		
		if (par1.getItem() instanceof ItemReactorUranium)
			return 10000 - par1.getItemDamage();
		
		return 0;
	}

	@Override
	public boolean isSteamReactor(TileEntity par1) {
		return par1 != null && par1 instanceof IReactor;
	}

	@Override
	public IC2Type getType() {
		return IC2Type.SPEIGER;
	}

	@Override
	public boolean isWrench(ItemStack par1) {
		return par1 != null && par1.getItem() instanceof ItemToolWrench;
	}

	@Override
	public EnergyStorageData getEnergyStorageData(TileEntity target) {
		if (target instanceof IEnergyStorage) {
			IEnergyStorage storage = (IEnergyStorage) target;
			EnergyStorageData result = new EnergyStorageData();
			result.values.add((double) storage.getCapacity());
			result.values.add((double) storage.getStored());
			result.type = EnergyStorageData.TARGET_TYPE_IC2;
			return result;
		}
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}
}
