package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.items.cards.ItemCardType;
import mekanism.common.tile.TileEntityFluidTank;
import mekanism.common.tile.TileEntityInductionCell;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidTank;

import java.util.ArrayList;
import java.util.List;

public class CrossMekanism extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof TileEntityInductionCell) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", ItemCardType.EU_RF);
			tag.setDouble("storage", ((TileEntityInductionCell) te).getEnergy());
			tag.setDouble("maxStorage", ((TileEntityInductionCell) te).getMaxEnergy());
			return tag;
		}
		return null;
	}

	@Override
	public List<IFluidTank> getAllTanks(TileEntity te) {
		if (te instanceof TileEntityFluidTank) {
			List<IFluidTank> result = new ArrayList<>();
			IFluidTank tank = ((TileEntityFluidTank) te).fluidTank;
			result.add(tank);
			return result;
		}
		return null;
	}
}
