package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityHoloPanelExtender extends TileEntityInfoPanelExtender {

	public TileEntityHoloPanelExtender(BlockPos pos, BlockState state) {
		super(ModTileEntityTypes.holo_panel_extender.get(), pos, state);
	}
}
