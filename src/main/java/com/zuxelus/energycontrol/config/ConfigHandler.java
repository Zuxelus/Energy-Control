package com.zuxelus.energycontrol.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.zuxelus.energycontrol.EnergyControl;

import net.fabricmc.loader.api.FabricLoader;

public class ConfigHandler {
	public static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), EnergyControl.MODID + ".config");

	public static int howlerAlarmRange = 64;
	public static int maxAlarmRange = 128;
	public static String allowedAlarms = "default,sci-fi,siren";
	public static int remoteThermalMonitorEnergyConsumption = 1;
	public static int screenRefreshPeriod = 20;
	public static int rangeTriggerRefreshPeriod = 20;
	public static int SMPMaxAlarmRange = 256;
	public static boolean useCustomSounds = false;

	public ConfigHandler() {
		loadConfig(CONFIG_FILE);
	}

	public static void loadConfig(File file) {
		try {
			Properties cfg = new Properties();
			if (!file.exists())
				saveConfig(file);
			cfg.load(new FileInputStream(file));
			howlerAlarmRange = Integer.parseInt(cfg.getProperty("howlerAlarmRange"));
			maxAlarmRange = Integer.parseInt(cfg.getProperty("maxAlarmRange"));
			allowedAlarms = cfg.getProperty("allowedAlarms");
			remoteThermalMonitorEnergyConsumption = Integer.parseInt(cfg.getProperty("remoteThermalMonitorEnergyConsumption"));
			screenRefreshPeriod = Integer.parseInt(cfg.getProperty("screenRefreshPeriod"));
			rangeTriggerRefreshPeriod = Integer.parseInt(cfg.getProperty("rangeTriggerRefreshPeriod"));
			SMPMaxAlarmRange = Integer.parseInt(cfg.getProperty("SMPMaxAlarmRange"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveConfig(File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file, false);
			fos.write(("howlerAlarmRange=" + howlerAlarmRange + "\n").getBytes());
			fos.write(("maxAlarmRange=" + maxAlarmRange + "\n").getBytes());
			fos.write(("allowedAlarms=" + allowedAlarms + "\n").getBytes());
			fos.write(("remoteThermalMonitorEnergyConsumption=" + remoteThermalMonitorEnergyConsumption + "\n").getBytes());
			fos.write(("screenRefreshPeriod=" + screenRefreshPeriod + "\n").getBytes());
			fos.write(("rangeTriggerRefreshPeriod=" + rangeTriggerRefreshPeriod + "\n").getBytes());
			fos.write(("SMPMaxAlarmRange=" + SMPMaxAlarmRange + "\n").getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
