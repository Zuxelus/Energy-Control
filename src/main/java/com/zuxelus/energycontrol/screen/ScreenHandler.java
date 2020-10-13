package com.zuxelus.energycontrol.screen;

import com.zuxelus.energycontrol.blockentities.HowlerAlarmBlockEntity;
import com.zuxelus.energycontrol.blockentities.IndustrialAlarmBlockEntity;

import net.minecraft.client.MinecraftClient;

public class ScreenHandler {

	public static void openHowlerAlarmScreen(HowlerAlarmBlockEntity be) {
		MinecraftClient.getInstance().openScreen(new HowlerAlarmScreen(be));
	}

	public static void openIndustrialAlarmScreen(IndustrialAlarmBlockEntity be) {
		MinecraftClient.getInstance().openScreen(new IndustrialAlarmScreen(be));
	}
}
