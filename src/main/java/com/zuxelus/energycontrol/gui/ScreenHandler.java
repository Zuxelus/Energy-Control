package com.zuxelus.energycontrol.gui;

import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;

import net.minecraft.client.Minecraft;

public class ScreenHandler {

	public static void openHowlerAlarmScreen(TileEntityHowlerAlarm be) {
		Minecraft.getInstance().displayGuiScreen(new GuiHowlerAlarm(be));
	}

	/*public static void openIndustrialAlarmScreen(TileEntityIndustrialAlarm be) {
		Minecraft.getInstance().displayGuiScreen(new GuiIndustrialAlarm(be));
	}*/
}
