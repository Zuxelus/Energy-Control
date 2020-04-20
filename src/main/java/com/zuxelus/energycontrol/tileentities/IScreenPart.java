package com.zuxelus.energycontrol.tileentities;

public interface IScreenPart {
	public void setScreen(Screen screen);

	public Screen getScreen();

	public void updateData();

	public void notifyBlockUpdate();
}
