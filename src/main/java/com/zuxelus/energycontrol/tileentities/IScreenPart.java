package com.zuxelus.energycontrol.tileentities;

public interface IScreenPart {

	void setScreen(Screen screen);

	Screen getScreen();

	void updateData();

	void updateTileEntity();
}
