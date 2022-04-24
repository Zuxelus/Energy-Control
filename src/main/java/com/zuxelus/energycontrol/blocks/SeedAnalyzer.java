package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntitySeedAnalyzer;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

public class SeedAnalyzer extends FacingHorizontalActiveEC {

	@Override
	public TileEntityFacing createTileEntity(int meta) {
		return new TileEntitySeedAnalyzer();
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_SEED_ANALYZER;
	}
}
