package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

public class KitAssembler extends FacingHorizontalActiveEC {

	@Override
	public TileEntityFacing createTileEntity(int meta) {
		return new TileEntityKitAssembler();
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.GUI_KIT_ASSEMBER;
	}
}
