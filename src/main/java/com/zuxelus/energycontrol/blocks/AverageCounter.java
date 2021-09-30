package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;
import com.zuxelus.zlib.tileentities.TileEntityFacing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class AverageCounter extends FacingBlock {

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityAverageCounter();
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_AVERAGE_COUNTER;
	}
}
