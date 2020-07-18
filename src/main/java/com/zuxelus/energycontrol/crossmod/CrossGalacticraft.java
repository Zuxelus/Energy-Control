package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC;
import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;

public class CrossGalacticraft extends CrossModBase {

	public CrossGalacticraft() {
		super("galacticraftplanets", null, null);
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

	public ItemStack getEnergyCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IEnergyHandlerGC) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		return null;
	}

	public FluidTankProperties[] getAllTanks(TileEntity te) {
		if (te instanceof IFluidHandlerWrapper) {
			FluidTankInfo[] info = ((IFluidHandlerWrapper) te).getTankInfo(null);
			FluidTankProperties[] result = FluidTankProperties.convert(info);
			if (result.length > 0)
				return result;
			for (EnumFacing facing : EnumFacing.VALUES)
				info = ((IFluidHandlerWrapper) te).getTankInfo(facing);
			return FluidTankProperties.convert(info);
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
