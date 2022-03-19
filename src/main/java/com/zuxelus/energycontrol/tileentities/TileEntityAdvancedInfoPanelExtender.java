package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

public class TileEntityAdvancedInfoPanelExtender extends TileEntityInfoPanelExtender {

	public TileEntityAdvancedInfoPanelExtender(TileEntityType<?> type) {
		super(type);
	}

	public TileEntityAdvancedInfoPanelExtender() {
		this(ModTileEntityTypes.info_panel_advanced_extender.get());
	}

	public byte getThickness() {
		if (screen == null)
			return 16;
		TileEntityInfoPanel core = screen.getCore();
		if (core == null || !(core instanceof TileEntityAdvancedInfoPanel))
			return 16;
		return ((TileEntityAdvancedInfoPanel) core).thickness;
	}

	public byte getRotateHor() {
		if (screen == null)
			return 0;
		TileEntityInfoPanel core = screen.getCore();
		if (core == null || !(core instanceof TileEntityAdvancedInfoPanel))
			return 0;
		return ((TileEntityAdvancedInfoPanel) core).rotateHor;
	}

	public byte getRotateVert() {
		if (screen == null)
			return 0;
		TileEntityInfoPanel core = screen.getCore();
		if (core == null || !(core instanceof TileEntityAdvancedInfoPanel))
			return 0;
		return ((TileEntityAdvancedInfoPanel) core).rotateVert;
	}

	@Override
	public Direction getRotation() {
		if (screen == null)
			return Direction.NORTH;
		TileEntityInfoPanel core = screen.getCore();
		if (core == null || !(core instanceof TileEntityAdvancedInfoPanel))
			return Direction.NORTH;
		return ((TileEntityAdvancedInfoPanel) core).getRotation();
	}
}
