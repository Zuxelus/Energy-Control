package com.zuxelus.energycontrol.tileentities;

import net.minecraft.util.EnumFacing;

public class TileEntityAdvancedInfoPanelExtender extends TileEntityInfoPanelExtender {

	public byte getThickness() {
		if (screen == null)
			return 16;
		TileEntityInfoPanel core = screen.getCore(world);
		if (!(core instanceof TileEntityAdvancedInfoPanel))
			return 16;
		return ((TileEntityAdvancedInfoPanel) core).thickness;
	}

	public byte getRotateHor() {
		if (screen == null)
			return 0;
		TileEntityInfoPanel core = screen.getCore(world);
		if (!(core instanceof TileEntityAdvancedInfoPanel))
			return 0;
		return ((TileEntityAdvancedInfoPanel) core).rotateHor;
	}

	public byte getRotateVert() {
		if (screen == null)
			return 0;
		TileEntityInfoPanel core = screen.getCore(world);
		if (!(core instanceof TileEntityAdvancedInfoPanel))
			return 0;
		return ((TileEntityAdvancedInfoPanel) core).rotateVert;
	}
}
