package com.zuxelus.energycontrol.config;

import com.zuxelus.energycontrol.EnergyControl;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

// Config screen built with Cloth Config. Only reached through ModMenu when Cloth Config is installed,
// so no other class may reference Cloth Config types.
public final class ClothConfigScreen {

	public static Screen create(Screen screen) {
		ConfigBuilder builder = ConfigBuilder.create().setParentScreen(screen)
				.setTitle(Component.translatable("title." + EnergyControl.MODID + ".config"));

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config." + EnergyControl.MODID + ".general"));

		general.addEntry(
				entryBuilder.startIntField(Component.translatable("config." + EnergyControl.MODID + ".howlerAlarmRange"), ConfigHandler.howlerAlarmRange)
						.setDefaultValue(64).setTooltip(Component.translatable("config." + EnergyControl.MODID + ".howlerAlarmRange.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.howlerAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startIntField(Component.translatable("config." + EnergyControl.MODID + ".maxAlarmRange"), ConfigHandler.maxAlarmRange)
						.setDefaultValue(128).setTooltip(Component.translatable("config." + EnergyControl.MODID + ".maxAlarmRange.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.maxAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startStrField(Component.translatable("config." + EnergyControl.MODID + ".allowedAlarms"), ConfigHandler.allowedAlarms)
						.setDefaultValue("default,sci-fi,siren").setTooltip(Component.translatable("config." + EnergyControl.MODID + ".allowedAlarms.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.allowedAlarms = value).build());
		general.addEntry(
				entryBuilder.startIntField(Component.translatable("config." + EnergyControl.MODID + ".screenRefreshPeriod"), ConfigHandler.screenRefreshPeriod)
						.setDefaultValue(20).setTooltip(Component.translatable("config." + EnergyControl.MODID + ".screenRefreshPeriod.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.screenRefreshPeriod = value).build());
		general.addEntry(
				entryBuilder.startIntField(Component.translatable("config." + EnergyControl.MODID + ".rangeTriggerRefreshPeriod"), ConfigHandler.rangeTriggerRefreshPeriod)
						.setDefaultValue(20).setTooltip(Component.translatable("config." + EnergyControl.MODID + ".rangeTriggerRefreshPeriod.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.rangeTriggerRefreshPeriod = value).build());
		general.addEntry(
				entryBuilder.startIntField(Component.translatable("config." + EnergyControl.MODID + ".thermalMonitorRefreshPeriod"), ConfigHandler.thermalMonitorRefreshPeriod)
						.setDefaultValue(20).setMin(1).setTooltip(Component.translatable("config." + EnergyControl.MODID + ".thermalMonitorRefreshPeriod.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.thermalMonitorRefreshPeriod = value).build());
		general.addEntry(
				entryBuilder.startIntField(Component.translatable("config." + EnergyControl.MODID + ".alarmPause"), ConfigHandler.alarmPause)
						.setDefaultValue(60).setMin(10).setMax(2000).setTooltip(Component.translatable("config." + EnergyControl.MODID + ".alarmPause.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.alarmPause = value).build());
		general.addEntry(
				entryBuilder.startBooleanToggle(Component.translatable("config." + EnergyControl.MODID + ".disableRangeCheck"), ConfigHandler.disableRangeCheck)
						.setDefaultValue(false).setTooltip(Component.translatable("config." + EnergyControl.MODID + ".disableRangeCheck.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.disableRangeCheck = value).build());
		general.addEntry(
				entryBuilder.startBooleanToggle(Component.translatable("config." + EnergyControl.MODID + ".wsEnabled"), ConfigHandler.wsEnabled)
						.setDefaultValue(false).setTooltip(Component.translatable("config." + EnergyControl.MODID + ".wsEnabled.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.wsEnabled = value).build());
		general.addEntry(
				entryBuilder.startStrField(Component.translatable("config." + EnergyControl.MODID + ".wsHost"), ConfigHandler.wsHost)
						.setDefaultValue("").setTooltip(Component.translatable("config." + EnergyControl.MODID + ".wsHost.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.wsHost = value).build());
		general.addEntry(
				entryBuilder.startIntField(Component.translatable("config." + EnergyControl.MODID + ".wsPort"), ConfigHandler.wsPort)
						.setDefaultValue(0).setMin(0).setMax(65535).setTooltip(Component.translatable("config." + EnergyControl.MODID + ".wsPort.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.wsPort = value).build());
		general.addEntry(
				entryBuilder.startStrField(Component.translatable("config." + EnergyControl.MODID + ".wsToken"), ConfigHandler.wsToken)
						.setDefaultValue("78c2b80a-1203-43fd-a9af-75cec29f5acf").setTooltip(Component.translatable("config." + EnergyControl.MODID + ".wsToken.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.wsToken = value).build());
		general.addEntry(
				entryBuilder.startIntField(Component.translatable("config." + EnergyControl.MODID + ".wsRefreshRate"), ConfigHandler.wsRefreshRate)
						.setDefaultValue(100).setMin(10).setMax(20000).setTooltip(Component.translatable("config." + EnergyControl.MODID + ".wsRefreshRate.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.wsRefreshRate = value).build());
		general.addEntry(
				entryBuilder.startStrField(Component.translatable("config." + EnergyControl.MODID + ".wsServerID"), ConfigHandler.wsServerId)
						.setDefaultValue(ConfigHandler.wsServerId).setTooltip(Component.translatable("config." + EnergyControl.MODID + ".wsServerID.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.wsServerId = value).build());
		general.addEntry(
				entryBuilder.startBooleanToggle(Component.translatable("config." + EnergyControl.MODID + ".useCustomSounds"), ConfigHandler.useCustomSounds)
						.setDefaultValue(true).setTooltip(Component.translatable("config." + EnergyControl.MODID + ".useCustomSounds.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.useCustomSounds = value).build());

		return builder.setSavingRunnable(() -> {
			ConfigHandler.saveConfig(ConfigHandler.CONFIG_FILE);
			ConfigHandler.loadConfig(ConfigHandler.CONFIG_FILE);
		}).build();
	}
}
