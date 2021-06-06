package com.zuxelus.energycontrol.crossmod;

import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC;
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import java.util.ArrayList;
import java.util.List;

public class CrossGalacticraft extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof IEnergyHandlerGC) {
			NBTTagCompound tag = new NBTTagCompound();
			IEnergyHandlerGC storage = (IEnergyHandlerGC) te;
			tag.setInteger("type", 11);
			tag.setDouble("storage", storage.getEnergyStoredGC(null));
			tag.setDouble("maxStorage", storage.getMaxEnergyStoredGC(null));
			return tag;
		}
		return null;
	}

	@Override
	public List<IFluidTank> getAllTanks(TileEntity te) {
		if (te instanceof IFluidHandlerWrapper) {
			FluidTankInfo[] info = ((IFluidHandlerWrapper) te).getTankInfo(null);
			List<IFluidTank> result = new ArrayList<>();
			for (FluidTankInfo tank : info)
				result.add(new FluidTank(tank.fluid, tank.capacity));
			if (result.size() > 0)
				return result;
			for (EnumFacing facing : EnumFacing.VALUES) {
				info = ((IFluidHandlerWrapper) te).getTankInfo(facing);
				for (FluidTankInfo tank : info)
					result.add(new FluidTank(tank.fluid, tank.capacity));
			}
			return result;
		}
		return null;
	}
}
