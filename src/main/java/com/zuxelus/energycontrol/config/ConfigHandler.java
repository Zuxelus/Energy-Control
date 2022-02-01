package com.zuxelus.energycontrol.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class ConfigHandler {
	public static final String CATEGORY_GENERAL = "General";

	public static ForgeConfigSpec COMMON_CONFIG;

	public static ForgeConfigSpec.IntValue HOWLER_ALARM_RANGE;
	public static ForgeConfigSpec.IntValue MAX_ALARM_RANGE;
	public static ForgeConfigSpec.ConfigValue<String> ALLOWED_ALARMS;
	public static ForgeConfigSpec.IntValue SCREEN_REFRESH_PERIOD;
	public static ForgeConfigSpec.IntValue RANGE_TRIGGER_REFRESH_PERIOD;
	public static ForgeConfigSpec.BooleanValue USE_CUSTOM_SOUNDS;
	public static ForgeConfigSpec.IntValue ALARM_PAUSE;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment("General settings").push(CATEGORY_GENERAL);
		HOWLER_ALARM_RANGE = builder.comment("Default Howler Alarm Range").defineInRange("howlerAlarmRange", 64, 0, Integer.MAX_VALUE);
		MAX_ALARM_RANGE = builder.comment("Max Possible Howler Alarm Range").defineInRange("maxAlarmRange", 128, 0, Integer.MAX_VALUE);
		ALLOWED_ALARMS = builder.comment("Allowed Alarms").define("allowedAlarms", "default,sci-fi,siren");
		SCREEN_REFRESH_PERIOD = builder.comment("Default Panel Refresh Period").defineInRange("infoPanelRefreshPeriod", 20, 1, Integer.MAX_VALUE);
		RANGE_TRIGGER_REFRESH_PERIOD = builder.comment("Default Range Trigger Refresh Period").defineInRange("rangeTriggerRefreshPeriod", 20, 1, Integer.MAX_VALUE);
		USE_CUSTOM_SOUNDS =  builder.comment("Load Custom Sounds").define("useCustomSounds", false);
		ALARM_PAUSE = builder.comment("Alarm Pause in Ticks").defineInRange("alarmPause", 60, 10, 2000);
		builder.pop();
		COMMON_CONFIG = builder.build();
	}
}
