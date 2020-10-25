package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AverageCounter extends FacingBlock {

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityAverageCounter te = new TileEntityAverageCounter();
		te.setFacing(meta);
		return te;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_AVERAGE_COUNTER;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(CrossModLoader.ic2.getItemStack("machine"));
	}
}
