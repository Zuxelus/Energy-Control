package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class EnergyCounter extends FacingBlock {

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityEnergyCounter();
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_ENERGY_COUNTER;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(CrossModLoader.ic2.getItemStack("machine"));
	}
}