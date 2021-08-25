package com.zuxelus.energycontrol.crossmod;

import mekanism.generators.common.tile.TileEntityGenerator;
import mekanism.generators.common.tile.TileEntityHeatGenerator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class CrossMekanismGenerators extends CrossModBase {

	@Override
	public CompoundNBT getEnergyData(TileEntity te) {
		if (te instanceof TileEntityGenerator) {
			CompoundNBT tag = CrossMekanism.setStorage(((TileEntityGenerator) te).getEnergyContainer());
			tag.putInt("type", 12);
			return tag;
		}
		return null;
	}

	@Override
	public CompoundNBT getCardData(TileEntity te) {
		if (te instanceof TileEntityGenerator) {
			CompoundNBT tag = CrossMekanism.setStorage(((TileEntityGenerator) te).getEnergyContainer());
			tag.putInt("type", 1);
			if (te instanceof TileEntityHeatGenerator)
				tag.putDouble("production", ((TileEntityHeatGenerator) te).getProducingEnergy().doubleValue());
			tag.putString("temp", CrossMekanism.getTempString(((TileEntityGenerator) te).getTotalTemperature()));
			return tag;
		}
		return null;
	}
}
