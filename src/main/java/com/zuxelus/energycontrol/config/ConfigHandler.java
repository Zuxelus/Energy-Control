package com.zuxelus.energycontrol.config;

import java.util.UUID;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public final class ConfigHandler {
	public static final String CATEGORY_GENERAL = "General";

	public static ForgeConfigSpec COMMON_CONFIG;

	public static ForgeConfigSpec.IntValue HOWLER_ALARM_RANGE;
	public static ForgeConfigSpec.IntValue MAX_ALARM_RANGE;
	public static ForgeConfigSpec.ConfigValue<String> ALLOWED_ALARMS;
	public static ForgeConfigSpec.IntValue SCREEN_REFRESH_PERIOD;
	public static ForgeConfigSpec.IntValue RANGE_TRIGGER_REFRESH_PERIOD;
	public static ForgeConfigSpec.IntValue THERMAL_MONITOR_REFRESH_PERIOD;
	public static ForgeConfigSpec.BooleanValue USE_CUSTOM_SOUNDS;
	public static ForgeConfigSpec.IntValue ALARM_PAUSE;
	public static ForgeConfigSpec.BooleanValue DISABLE_RANGE_CHECK;
	public static ForgeConfigSpec.BooleanValue WS_ENABLED;
	public static ForgeConfigSpec.ConfigValue<String> WS_HOST;
	public static ForgeConfigSpec.IntValue WS_PORT;
	public static ForgeConfigSpec.ConfigValue<String> WS_TOKEN;
	public static ForgeConfigSpec.IntValue WS_REFRESH_RATE;
	public static ForgeConfigSpec.ConfigValue<String> WS_SERVER_ID;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment("General settings").push(CATEGORY_GENERAL);
		HOWLER_ALARM_RANGE = builder.comment("Default Howler Alarm Range").defineInRange("howlerAlarmRange", 64, 0, Integer.MAX_VALUE);
		MAX_ALARM_RANGE = builder.comment("Max Possible Howler Alarm Range").defineInRange("maxAlarmRange", 128, 0, Integer.MAX_VALUE);
		ALLOWED_ALARMS = builder.comment("Allowed Alarms").define("allowedAlarms", "default,sci-fi,siren");
		SCREEN_REFRESH_PERIOD = builder.comment("Default Panel Refresh Period").defineInRange("infoPanelRefreshPeriod", 20, 1, Integer.MAX_VALUE);
		RANGE_TRIGGER_REFRESH_PERIOD = builder.comment("Default Range Trigger Refresh Period").defineInRange("rangeTriggerRefreshPeriod", 20, 1, Integer.MAX_VALUE);
		THERMAL_MONITOR_REFRESH_PERIOD = builder.comment("Default Thermal Monitor Refresh Period").defineInRange("thermalMonitorRefreshPeriod", 20, 1, Integer.MAX_VALUE);
		USE_CUSTOM_SOUNDS =  builder.comment("Load Custom Sounds").define("useCustomSounds", false);
		ALARM_PAUSE = builder.comment("Alarm Pause in Ticks").defineInRange("alarmPause", 60, 10, 2000);
		DISABLE_RANGE_CHECK =  builder.comment("Disable Range Check").define("disableRangeCheck", false);

		WS_ENABLED =  builder.comment("Web Socket Enabled").define("wsEnabled", false);
		WS_HOST =  builder.comment("Web Socket Host").define("wsHost", "");
		WS_PORT =  builder.comment("Web Socket Port").defineInRange("wsPort", 0, 1, 99999);
		WS_TOKEN =  builder.comment("Web Socket Token").define("wsToken", "78c2b80a-1203-43fd-a9af-75cec29f5acf");
		WS_REFRESH_RATE =  builder.comment("Web Socket Refresh Rate").defineInRange("wsRefreshRate", 100, 10, 20000);
		WS_SERVER_ID =  builder.comment("Web Socket Server ID").define("wsServerID", UUID.randomUUID().toString());
		builder.pop();
		COMMON_CONFIG = builder.build();
	}

	@SubscribeEvent
	public static void onModConfigEvent(ModConfig.ModConfigEvent event) {
		
	}
}
