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
	public static ForgeConfigSpec.IntValue REMOTE_MONITOR_ENERGY_CONSUMPTION;
	public static ForgeConfigSpec.IntValue SCREEN_REFRESH_PERIOD;
	public static ForgeConfigSpec.IntValue RANGE_TRIGGER_REFRESH_PERIOD;
	public static ForgeConfigSpec.BooleanValue USE_CUSTOM_SOUNDS;

	static {

		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.comment("General settings").push(CATEGORY_GENERAL);

		HOWLER_ALARM_RANGE = builder
				.translation("ec.config.howlerAlarmRange")
				.defineInRange("howlerAlarmRange", 64, 0, Integer.MAX_VALUE);

		MAX_ALARM_RANGE = builder
				.translation("ec.config.maxAlarmRange")
				.defineInRange("maxAlarmRange", 128, 0, Integer.MAX_VALUE);

		ALLOWED_ALARMS = builder
				.translation("ec.config.allowedAlarms")
				.define("allowedAlarms", "default,sci-fi,siren");

		REMOTE_MONITOR_ENERGY_CONSUMPTION = builder
				.translation("ec.config.remoteThermalMonitorEnergyConsumption")
				.defineInRange("remoteThermalMonitorEnergyConsumption", 1, 1, Integer.MAX_VALUE);

		SCREEN_REFRESH_PERIOD = builder
				.translation("ec.config.screenRefreshPeriod")
				.defineInRange("infoPanelRefreshPeriod", 20, 1, Integer.MAX_VALUE);

		RANGE_TRIGGER_REFRESH_PERIOD = builder
				.translation("ec.config.rangeTriggerRefreshPeriod")
				.defineInRange("rangeTriggerRefreshPeriod", 20, 1, Integer.MAX_VALUE);

		USE_CUSTOM_SOUNDS =  builder
				.translation("ec.config.useCustomSounds")
				.define("useCustomSounds", false);
		builder.pop();

		COMMON_CONFIG = builder.build();
	}

	/*@SubscribeEvent // TODO
	public static void onModConfigEvent(ModConfig.ModConfigEvent event) {
		
	}*/
}
