package com.zuxelus.energycontrol.config;

import java.util.function.Function;

import com.zuxelus.energycontrol.EnergyControl;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;

public class ModMenu implements ModMenuApi {

	@Override
	public String getModId() {
		return EnergyControl.MODID;
	}

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return this::create;
	}

	public Screen create(Screen screen) {
		ConfigBuilder builder = ConfigBuilder.create().setParentScreen(screen)
				.setTitle("title." + EnergyControl.MODID + ".config");

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		ConfigCategory general = builder.getOrCreateCategory("config." + EnergyControl.MODID + ".general");

		general.addEntry(
				entryBuilder.startIntField("config." + EnergyControl.MODID + ".howlerAlarmRange", ConfigHandler.howlerAlarmRange)
						.setDefaultValue(64).setTooltip("howlerAlarmRange")
						.setSaveConsumer(value -> ConfigHandler.howlerAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startIntField("config." + EnergyControl.MODID + ".maxAlarmRange", ConfigHandler.maxAlarmRange)
						.setDefaultValue(128).setTooltip("maxAlarmRange")
						.setSaveConsumer(value -> ConfigHandler.maxAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startStrField("config." + EnergyControl.MODID + ".allowedAlarms", ConfigHandler.allowedAlarms)
						.setDefaultValue("default,sci-fi,siren").setTooltip("allowedAlarms")
						.setSaveConsumer(value -> ConfigHandler.allowedAlarms = value).build());
		general.addEntry(
				entryBuilder.startIntField("config." + EnergyControl.MODID + ".remoteThermalMonitorEnergyConsumption", ConfigHandler.remoteThermalMonitorEnergyConsumption)
						.setDefaultValue(1).setTooltip("remoteThermalMonitorEnergyConsumption")
						.setSaveConsumer(value -> ConfigHandler.remoteThermalMonitorEnergyConsumption = value).build());
		general.addEntry(
				entryBuilder.startIntField("config." + EnergyControl.MODID + ".infoPanelRefreshPeriod", ConfigHandler.infoPanelRefreshPeriod)
						.setDefaultValue(20).setTooltip("infoPanelRefreshPeriod")
						.setSaveConsumer(value -> ConfigHandler.infoPanelRefreshPeriod = value).build());
		general.addEntry(
				entryBuilder.startIntField("config." + EnergyControl.MODID + ".rangeTriggerRefreshPeriod", ConfigHandler.rangeTriggerRefreshPeriod)
						.setDefaultValue(20).setTooltip("rangeTriggerRefreshPeriod")
						.setSaveConsumer(value -> ConfigHandler.rangeTriggerRefreshPeriod = value).build());
		general.addEntry(
				entryBuilder.startIntField("config." + EnergyControl.MODID + ".SMPMaxAlarmRange", ConfigHandler.SMPMaxAlarmRange)
						.setDefaultValue(256).setTooltip("SMPMaxAlarmRange")
						.setSaveConsumer(value -> ConfigHandler.SMPMaxAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startBooleanToggle("config." + EnergyControl.MODID + ".useCustomSounds", ConfigHandler.useCustomSounds)
						.setDefaultValue(true).setTooltip("useCustomSounds")
						.setSaveConsumer(value -> ConfigHandler.useCustomSounds = value).build());

		return builder.setSavingRunnable(() -> {
			ConfigHandler.saveConfig(ConfigHandler.CONFIG_FILE);
			ConfigHandler.loadConfig(ConfigHandler.CONFIG_FILE);
		}).build();
	}
}
