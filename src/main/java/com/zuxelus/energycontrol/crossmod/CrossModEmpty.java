package com.zuxelus.energycontrol.crossmod;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;

public class CrossModEmpty extends CrossMod {

	@Override
	public ItemStack getEnergyCard(World world, BlockPos pos) {
		return ItemStack.EMPTY;
	}

	@Override
	public CompoundNBT getEnergyData(TileEntity te) {
		return null;
	}

	@Override
	public List<IFluidTank> getAllTanks(TileEntity te) {
		return null;
	}
}
