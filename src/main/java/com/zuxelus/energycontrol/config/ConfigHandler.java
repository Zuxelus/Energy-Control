package com.zuxelus.energycontrol.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Function;

import com.zuxelus.energycontrol.EnergyControl;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

public class ConfigHandler implements ModMenuApi {
	public static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDirectory(), EnergyControl.MODID + ".config");

	public static int howlerAlarmRange = 64;
	public static int maxAlarmRange = 128;
	public static String allowedAlarms = "default,sci-fi,siren";
	public static int remoteThermalMonitorEnergyConsumption = 1;
	public static int infoPanelRefreshPeriod = 20;
	public static int rangeTriggerRefreshPeriod = 20;
	public static int SMPMaxAlarmRange = 256;

	public ConfigHandler() {
		loadConfig(CONFIG_FILE);
	}

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
				entryBuilder.startIntField("config." + EnergyControl.MODID + ".howlerAlarmRange", howlerAlarmRange)
						.setDefaultValue(64).setTooltip("howlerAlarmRange")
						.setSaveConsumer(value -> howlerAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startIntField("config." + EnergyControl.MODID + ".maxAlarmRange", maxAlarmRange)
						.setDefaultValue(128).setTooltip("maxAlarmRange")
						.setSaveConsumer(value -> maxAlarmRange = value).build());
		general.addEntry(
				entryBuilder.startStrField("config." + EnergyControl.MODID + ".allowedAlarms", allowedAlarms)
						.setDefaultValue("default,sci-fi,siren").setTooltip("allowedAlarms")
						.setSaveConsumer(value -> allowedAlarms = value).build());
		general.addEntry(
				entryBuilder.startIntField("config." + EnergyControl.MODID + ".remoteThermalMonitorEnergyConsumption", remoteThermalMonitorEnergyConsumption)
						.setDefaultValue(1).setTooltip("remoteThermalMonitorEnergyConsumption")
						.setSaveConsumer(value -> remoteThermalMonitorEnergyConsumption = value).build());
		general.addEntry(
				entryBuilder.startIntField("config." + EnergyControl.MODID + ".infoPanelRefreshPeriod", infoPanelRefreshPeriod)
						.setDefaultValue(20).setTooltip("infoPanelRefreshPeriod")
						.setSaveConsumer(value -> infoPanelRefreshPeriod = value).build());
		general.addEntry(
				entryBuilder.startIntField("config." + EnergyControl.MODID + ".rangeTriggerRefreshPeriod", rangeTriggerRefreshPeriod)
						.setDefaultValue(20).setTooltip("rangeTriggerRefreshPeriod")
						.setSaveConsumer(value -> rangeTriggerRefreshPeriod = value).build());
		general.addEntry(
				entryBuilder.startIntField("config." + EnergyControl.MODID + ".SMPMaxAlarmRange", SMPMaxAlarmRange)
						.setDefaultValue(256).setTooltip("SMPMaxAlarmRange")
						.setSaveConsumer(value -> SMPMaxAlarmRange = value).build());
		
		return builder.setSavingRunnable(() -> {
			saveConfig(CONFIG_FILE);
			loadConfig(CONFIG_FILE);
		}).build();
	}

	public void loadConfig(File file) {
		try {
			Properties cfg = new Properties();
			if (!file.exists())
				saveConfig(file);
			cfg.load(new FileInputStream(file));
			howlerAlarmRange = Integer.parseInt(cfg.getProperty("howlerAlarmRange"));
			maxAlarmRange = Integer.parseInt(cfg.getProperty("maxAlarmRange"));
			allowedAlarms = cfg.getProperty("allowedAlarms");
			remoteThermalMonitorEnergyConsumption = Integer.parseInt(cfg.getProperty("remoteThermalMonitorEnergyConsumption"));
			infoPanelRefreshPeriod = Integer.parseInt(cfg.getProperty("infoPanelRefreshPeriod"));
			rangeTriggerRefreshPeriod = Integer.parseInt(cfg.getProperty("rangeTriggerRefreshPeriod"));
			SMPMaxAlarmRange = Integer.parseInt(cfg.getProperty("SMPMaxAlarmRange"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveConfig(File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file, false);
			fos.write(("howlerAlarmRange=" + howlerAlarmRange + "\n").getBytes());
			fos.write(("maxAlarmRange=" + maxAlarmRange + "\n").getBytes());
			fos.write(("allowedAlarms=" + allowedAlarms + "\n").getBytes());
			fos.write(("remoteThermalMonitorEnergyConsumption=" + remoteThermalMonitorEnergyConsumption + "\n").getBytes());
			fos.write(("infoPanelRefreshPeriod=" + infoPanelRefreshPeriod + "\n").getBytes());
			fos.write(("rangeTriggerRefreshPeriod=" + rangeTriggerRefreshPeriod + "\n").getBytes());
			fos.write(("SMPMaxAlarmRange=" + SMPMaxAlarmRange + "\n").getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
