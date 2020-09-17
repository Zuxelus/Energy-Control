package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

public class CrossGalacticraft extends CrossModBase {

	public CrossGalacticraft() {
		super("GalacticraftMars", null, null);
	}

	public NBTTagCompound getEnergyData(TileEntity te) {
		if (!modLoaded)
			return null;

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

	public ItemStack getEnergyCard(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IEnergyHandlerGC) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY);
			ItemStackHelper.setCoordinates(sensorLocationCard, x, y, z);
			return sensorLocationCard;
		}
		return null;
	}

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

	public ItemStack getItemStack(String name) {
		if (!modLoaded)
			return null;

		switch (name) {
		case "aluminum_wire":
			return new ItemStack(GCBlocks.aluminumWire);
		}
		return null;
	}
}
