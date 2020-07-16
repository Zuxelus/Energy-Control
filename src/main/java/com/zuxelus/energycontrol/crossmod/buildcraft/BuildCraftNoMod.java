package com.zuxelus.energycontrol.crossmod.buildcraft;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;

public class BuildCraftNoMod extends CrossBC {

	@Override
	public boolean modLoaded() {
		return false;
	}

	@Override
	public List<IFluidTank> getAllTanks(TileEntity te) {
		return null;
	}

	@Override
	public ItemStack getGeneratorCard(World world, BlockPos pos) {
		return ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound getEngineData(TileEntity entity) {
		return null;
	}

}
