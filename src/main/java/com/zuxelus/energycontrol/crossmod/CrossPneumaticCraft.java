package com.zuxelus.energycontrol.crossmod;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrossPneumaticCraft extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		return null;
	}

	@Override
	public NBTTagCompound getCardData(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		return null;
	}
}
