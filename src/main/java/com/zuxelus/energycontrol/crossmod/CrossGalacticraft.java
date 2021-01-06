package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;

import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class CrossGalacticraft extends CrossModBase {

	@Override
	public ItemStack getItemStack(String name) {
		switch (name) {
		case "aluminum_wire":
			return new ItemStack(GCBlocks.aluminumWire);
		}
		return null;
	}

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
	public FluidTankInfo[] getAllTanks(TileEntity te) {
		if (te instanceof IFluidHandler) {
			FluidTankInfo[] info = ((IFluidHandler) te).getTankInfo(null);
			if (info.length > 0)
				return info;
			ArrayList<FluidTankInfo> list = new ArrayList<FluidTankInfo>();
			for (ForgeDirection facing : ForgeDirection.VALID_DIRECTIONS) {
				info = ((IFluidHandler) te).getTankInfo(facing);
				if (info.length > 0)
					for (FluidTankInfo tank : info)
						list.add(tank);
			}
			FluidTankInfo[] result = new FluidTankInfo[list.size()];
			return list.toArray(result);
		}
		return null;
	}
}
