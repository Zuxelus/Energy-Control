package com.zuxelus.energycontrol.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public class ConfigHandler {
	public static final String CATEGORY_GENERAL = "General";

	public static ForgeConfigSpec COMMON_CONFIG;

	public static ForgeConfigSpec.IntValue HOWLER_ALARM_RANGE;
	public static ForgeConfigSpec.IntValue MAX_ALARM_RANGE;
	public static ForgeConfigSpec.ConfigValue<String> ALLOWED_ALARMS;
	public static ForgeConfigSpec.IntValue REMOTE_MONITOR_ENERGY_CONSUMPTION;
	public static ForgeConfigSpec.IntValue SCREEN_REFRESH_PERIOD;
	public static ForgeConfigSpec.IntValue RANGE_TRIGGER_REFRESH_PERIOD;
	public static ForgeConfigSpec.IntValue SMP_MAX_ALARM_RANGE;
	public static ForgeConfigSpec.BooleanValue USE_CUSTOM_SOUNDS;

	static {

		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.comment("General settings").push(CATEGORY_GENERAL);

		HOWLER_ALARM_RANGE = builder.comment("Maximum power for the FirstBlock generator")
				.translation("")
				.defineInRange("howlerAlarmRange", 64, 0, Integer.MAX_VALUE);

		MAX_ALARM_RANGE = builder.comment("Maximum power for the FirstBlock generator")
				.translation("")
				.defineInRange("maxAlarmRange", 128, 0, Integer.MAX_VALUE);

		ALLOWED_ALARMS = builder.comment("Maximum power for the FirstBlock generator")
				.translation("")
				.define("allowedAlarms", "default,sci-fi,siren");

		REMOTE_MONITOR_ENERGY_CONSUMPTION = builder.comment("Maximum power for the FirstBlock generator")
				.translation("")
				.defineInRange("remoteThermalMonitorEnergyConsumption", 1, 1, Integer.MAX_VALUE);

		SCREEN_REFRESH_PERIOD = builder.comment("Maximum power for the FirstBlock generator")
				.translation("")
				.defineInRange("infoPanelRefreshPeriod", 20, 1, Integer.MAX_VALUE);

		RANGE_TRIGGER_REFRESH_PERIOD = builder.comment("Maximum power for the FirstBlock generator")
				.translation("")
				.defineInRange("rangeTriggerRefreshPeriod", 20, 1, Integer.MAX_VALUE);

		SMP_MAX_ALARM_RANGE = builder.comment("Maximum power for the FirstBlock generator")
				.translation("")
				.defineInRange("SMPMaxAlarmRange", 256, 0, Integer.MAX_VALUE);
		
		USE_CUSTOM_SOUNDS =  builder.comment("Maximum power for the FirstBlock generator")
				.translation("")
				.define("useCustomSounds", false);
		builder.pop();

		COMMON_CONFIG = builder.build();
	}

	@SubscribeEvent
	public static void onModConfigEvent(ModConfig.ModConfigEvent event) {
		
	}
}
