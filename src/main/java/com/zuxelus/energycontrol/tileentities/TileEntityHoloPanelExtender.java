package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class TileEntityHoloPanelExtender extends TileEntityInfoPanelExtender {

	public TileEntityHoloPanelExtender(BlockPos pos, BlockState state) {
		super(ModTileEntityTypes.holo_panel_extender, pos, state);
	}
}
