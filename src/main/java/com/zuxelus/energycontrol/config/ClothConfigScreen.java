package com.zuxelus.energycontrol.config;

import net.minecraft.text.Text;

import com.zuxelus.energycontrol.EnergyControl;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;

// Config screen built with Cloth Config. Only reached through ModMenu when Cloth Config is installed,
// so no other class may reference Cloth Config types.
public final class ClothConfigScreen {

	public static Screen create(Screen screen) {
		ConfigBuilder builder = ConfigBuilder.create().setParentScreen(screen)
				.setTitle(Text.translatable("title." + EnergyControl.MODID + ".config"));

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config." + EnergyControl.MODID + ".general"));

		general.addEntry(
				entryBuilder.startIntField(Text.translatable("config." + EnergyControl.MODID + ".howlerAlarmRange"), ConfigHandler.howlerAlarmRange)
						.setDefaultValue(64).setTooltip(Text.translatable("config." + EnergyControl.MODID + ".howlerAlarmRange.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.howlerAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startIntField(Text.translatable("config." + EnergyControl.MODID + ".maxAlarmRange"), ConfigHandler.maxAlarmRange)
						.setDefaultValue(128).setTooltip(Text.translatable("config." + EnergyControl.MODID + ".maxAlarmRange.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.maxAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startStrField(Text.translatable("config." + EnergyControl.MODID + ".allowedAlarms"), ConfigHandler.allowedAlarms)
						.setDefaultValue("default,sci-fi,siren").setTooltip(Text.translatable("config." + EnergyControl.MODID + ".allowedAlarms.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.allowedAlarms = value).build());
		general.addEntry(
				entryBuilder.startIntField(Text.translatable("config." + EnergyControl.MODID + ".remoteThermalMonitorEnergyConsumption"), ConfigHandler.remoteThermalMonitorEnergyConsumption)
						.setDefaultValue(1).setTooltip(Text.translatable("config." + EnergyControl.MODID + ".remoteThermalMonitorEnergyConsumption.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.remoteThermalMonitorEnergyConsumption = value).build());
		general.addEntry(
				entryBuilder.startIntField(Text.translatable("config." + EnergyControl.MODID + ".screenRefreshPeriod"), ConfigHandler.screenRefreshPeriod)
						.setDefaultValue(20).setTooltip(Text.translatable("config." + EnergyControl.MODID + ".screenRefreshPeriod.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.screenRefreshPeriod = value).build());
		general.addEntry(
				entryBuilder.startIntField(Text.translatable("config." + EnergyControl.MODID + ".rangeTriggerRefreshPeriod"), ConfigHandler.rangeTriggerRefreshPeriod)
						.setDefaultValue(20).setTooltip(Text.translatable("config." + EnergyControl.MODID + ".rangeTriggerRefreshPeriod.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.rangeTriggerRefreshPeriod = value).build());
		general.addEntry(
				entryBuilder.startIntField(Text.translatable("config." + EnergyControl.MODID + ".SMPMaxAlarmRange"), ConfigHandler.SMPMaxAlarmRange)
						.setDefaultValue(256).setTooltip(Text.translatable("config." + EnergyControl.MODID + ".SMPMaxAlarmRange.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.SMPMaxAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startBooleanToggle(Text.translatable("config." + EnergyControl.MODID + ".useCustomSounds"), ConfigHandler.useCustomSounds)
						.setDefaultValue(true).setTooltip(Text.translatable("config." + EnergyControl.MODID + ".useCustomSounds.tooltip"))
						.setSaveConsumer(value -> ConfigHandler.useCustomSounds = value).build());

		return builder.setSavingRunnable(() -> {
			ConfigHandler.saveConfig(ConfigHandler.CONFIG_FILE);
			ConfigHandler.loadConfig(ConfigHandler.CONFIG_FILE);
		}).build();
	}
}
