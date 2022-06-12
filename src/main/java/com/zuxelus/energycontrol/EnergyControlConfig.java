package com.zuxelus.energycontrol;

import java.util.UUID;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = EnergyControl.MODID)
@LangKey("ec.config.title")
@EventBusSubscriber(modid = EnergyControl.MODID)
public class EnergyControlConfig {

	@RangeInt(min = 0, max = 256)
	@LangKey("ec.config.howlerAlarmRange")
	public static int howlerAlarmRange = 64;

	@RangeInt(min = 0, max = 256)
	@LangKey("ec.config.maxAlarmRange")
	public static int maxAlarmRange = 128;

	@LangKey("ec.config.allowedAlarms")
	public static String[] allowedAlarms = new String[] { "default", "sci-fi", "siren" };

	@RangeInt(min = 0, max = 2000)
	@LangKey("ec.config.infoPanelRefreshPeriod")
	public static int infoPanelRefreshPeriod = 20;

	@RangeInt(min = 0, max = 2000)
	@LangKey("ec.config.rangeTriggerRefreshPeriod")
	public static int rangeTriggerRefreshPeriod = 20;

	@RangeInt(min = 0, max = 2000)
	@LangKey("ec.config.thermalMonitorRefreshPeriod")
	public static int thermalMonitorRefreshPeriod = 20;

	@LangKey("ec.config.useCustomSounds")
	public static boolean useCustomSounds = false;

	@LangKey("ec.config.alarmPause")
	public static int alarmPause = 60;

	@LangKey("ec.config.disableRangeCheck")
	public static boolean disableRangeCheck = false;

	@LangKey("ec.config.showOreInfo")
	public static boolean showOreInfo = true;

	public static WebSocket webSocket = new WebSocket();

	public static class WebSocket {

		@LangKey("ec.config.wsEnabled")
		public boolean wsEnabled = false;
	
		@LangKey("ec.config.wsHost")
		public String wsHost = "";
	
		@LangKey("ec.config.wsPort")
		public int wsPort = 0;
	
		@LangKey("ec.config.wsToken")
		public String wsToken = "78c2b80a-1203-43fd-a9af-75cec29f5acf";
	
		@LangKey("ec.config.wsRefreshRate")
		public int wsRefreshRate = 100;
	
		@LangKey("ec.config.wsServerID")
		public String wsServerID = UUID.randomUUID().toString();
	}

	@SubscribeEvent
	public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(EnergyControl.MODID)) {
			ConfigManager.sync(EnergyControl.MODID, Config.Type.INSTANCE);
		}
	}
}
