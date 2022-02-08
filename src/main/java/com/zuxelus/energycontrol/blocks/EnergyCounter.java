package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

public class EnergyCounter extends FacingBlock {

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityEnergyCounter();
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_ENERGY_COUNTER;
	}
}
