package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

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
