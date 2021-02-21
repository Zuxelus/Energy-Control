package com.zuxelus.energycontrol.crossmod;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;

public abstract class CrossMod {

	public abstract ItemStack getEnergyCard(World world, BlockPos pos);

	public abstract CompoundNBT getEnergyData(TileEntity te);
	
	public abstract List<IFluidTank> getAllTanks(TileEntity te);
}
