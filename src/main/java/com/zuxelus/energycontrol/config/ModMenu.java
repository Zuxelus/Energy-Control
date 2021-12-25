package com.zuxelus.energycontrol.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.zuxelus.energycontrol.EnergyControl;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class ModMenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return this::create;
	}

	public Screen create(Screen screen) {
		ConfigBuilder builder = ConfigBuilder.create().setParentScreen(screen)
				.setTitle(new TranslatableText("title." + EnergyControl.MODID + ".config"));

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("config." + EnergyControl.MODID + ".general"));

		general.addEntry(
				entryBuilder.startIntField(new TranslatableText("config." + EnergyControl.MODID + ".howlerAlarmRange"), ConfigHandler.howlerAlarmRange)
						.setDefaultValue(64).setTooltip(new LiteralText("howlerAlarmRange"))
						.setSaveConsumer(value -> ConfigHandler.howlerAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startIntField(new TranslatableText("config." + EnergyControl.MODID + ".maxAlarmRange"), ConfigHandler.maxAlarmRange)
						.setDefaultValue(128).setTooltip(new LiteralText("maxAlarmRange"))
						.setSaveConsumer(value -> ConfigHandler.maxAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startStrField(new TranslatableText("config." + EnergyControl.MODID + ".allowedAlarms"), ConfigHandler.allowedAlarms)
						.setDefaultValue("default,sci-fi,siren").setTooltip(new LiteralText("allowedAlarms"))
						.setSaveConsumer(value -> ConfigHandler.allowedAlarms = value).build());
		general.addEntry(
				entryBuilder.startIntField(new TranslatableText("config." + EnergyControl.MODID + ".remoteThermalMonitorEnergyConsumption"), ConfigHandler.remoteThermalMonitorEnergyConsumption)
						.setDefaultValue(1).setTooltip(new LiteralText("remoteThermalMonitorEnergyConsumption"))
						.setSaveConsumer(value -> ConfigHandler.remoteThermalMonitorEnergyConsumption = value).build());
		general.addEntry(
				entryBuilder.startIntField(new TranslatableText("config." + EnergyControl.MODID + ".screenRefreshPeriod"), ConfigHandler.screenRefreshPeriod)
						.setDefaultValue(20).setTooltip(new LiteralText("screenRefreshPeriod"))
						.setSaveConsumer(value -> ConfigHandler.screenRefreshPeriod = value).build());
		general.addEntry(
				entryBuilder.startIntField(new TranslatableText("config." + EnergyControl.MODID + ".rangeTriggerRefreshPeriod"), ConfigHandler.rangeTriggerRefreshPeriod)
						.setDefaultValue(20).setTooltip(new LiteralText("rangeTriggerRefreshPeriod"))
						.setSaveConsumer(value -> ConfigHandler.rangeTriggerRefreshPeriod = value).build());
		general.addEntry(
				entryBuilder.startIntField(new TranslatableText("config." + EnergyControl.MODID + ".SMPMaxAlarmRange"), ConfigHandler.SMPMaxAlarmRange)
						.setDefaultValue(256).setTooltip(new LiteralText("SMPMaxAlarmRange"))
						.setSaveConsumer(value -> ConfigHandler.SMPMaxAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startBooleanToggle(new TranslatableText("config." + EnergyControl.MODID + ".useCustomSounds"), ConfigHandler.useCustomSounds)
						.setDefaultValue(true).setTooltip(new LiteralText("useCustomSounds"))
						.setSaveConsumer(value -> ConfigHandler.useCustomSounds = value).build());

		return builder.setSavingRunnable(() -> {
			ConfigHandler.saveConfig(ConfigHandler.CONFIG_FILE);
			ConfigHandler.loadConfig(ConfigHandler.CONFIG_FILE);
		}).build();
	}
}
