package com.zuxelus.energycontrol.tileentities;

public class TileEntityAdvancedInfoPanelExtender extends TileEntityInfoPanelExtender {

	public byte getThickness() {
		if (screen == null)
			return 16;
		TileEntityInfoPanel core = screen.getCore(world);
		if (core == null || !(core instanceof TileEntityAdvancedInfoPanel))
			return 16;
		return ((TileEntityAdvancedInfoPanel)core).thickness;
	}
}
