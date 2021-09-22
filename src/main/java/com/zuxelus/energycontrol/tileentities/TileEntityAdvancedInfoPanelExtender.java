package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityAdvancedInfoPanelExtender extends TileEntityInfoPanelExtender {

	public TileEntityAdvancedInfoPanelExtender(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TileEntityAdvancedInfoPanelExtender(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.info_panel_advanced_extender.get(), pos, state);
	}

	public byte getThickness() {
		if (screen == null)
			return 16;
		TileEntityInfoPanel core = screen.getCore(level);
		if (core == null || !(core instanceof TileEntityAdvancedInfoPanel))
			return 16;
		return ((TileEntityAdvancedInfoPanel) core).thickness;
	}

	public byte getRotateHor() {
		if (screen == null)
			return 0;
		TileEntityInfoPanel core = screen.getCore(level);
		if (core == null || !(core instanceof TileEntityAdvancedInfoPanel))
			return 0;
		return ((TileEntityAdvancedInfoPanel) core).rotateHor;
	}

	public byte getRotateVert() {
		if (screen == null)
			return 0;
		TileEntityInfoPanel core = screen.getCore(level);
		if (core == null || !(core instanceof TileEntityAdvancedInfoPanel))
			return 0;
		return ((TileEntityAdvancedInfoPanel) core).rotateVert;
	}

	@Override
	public Direction getRotation() {
		if (screen == null)
			return Direction.NORTH;
		TileEntityInfoPanel core = screen.getCore(level);
		if (core == null || !(core instanceof TileEntityAdvancedInfoPanel))
			return Direction.NORTH;
		return ((TileEntityAdvancedInfoPanel) core).getRotation();
	}
}
