package com.zuxelus.energycontrol.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

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
	public static boolean disableRangeCheck = false;
	public static boolean wsEnabled = false;
	public static String wsHost = "";
	public static int wsPort = 0;
	public static String wsToken = "78c2b80a-1203-43fd-a9af-75cec29f5acf";
	public static int wsRefreshRate = 100;
	public static String wsServerId = UUID.randomUUID().toString();

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
			useCustomSounds = Boolean.parseBoolean(cfg.getProperty("useCustomSounds"));
			disableRangeCheck = Boolean.parseBoolean(cfg.getProperty("disableRangeCheck", String.valueOf(disableRangeCheck)));
			// older config files have no web socket keys: keep the defaults, and the generated server id, and write them out
			boolean hasWebSocketKeys = cfg.getProperty("wsServerID") != null;
			wsEnabled = Boolean.parseBoolean(cfg.getProperty("wsEnabled", String.valueOf(wsEnabled)));
			wsHost = cfg.getProperty("wsHost", wsHost);
			wsPort = Math.max(0, Math.min(65535, Integer.parseInt(cfg.getProperty("wsPort", String.valueOf(wsPort)))));
			wsToken = cfg.getProperty("wsToken", wsToken);
			wsRefreshRate = Math.max(10, Math.min(20000, Integer.parseInt(cfg.getProperty("wsRefreshRate", String.valueOf(wsRefreshRate)))));
			wsServerId = cfg.getProperty("wsServerID", wsServerId);
			if (!hasWebSocketKeys)
				saveConfig(file);
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
			fos.write(("useCustomSounds=" + useCustomSounds + "\n").getBytes());
			fos.write(("disableRangeCheck=" + disableRangeCheck + "\n").getBytes());
			fos.write(("wsEnabled=" + wsEnabled + "\n").getBytes());
			fos.write(("wsHost=" + wsHost + "\n").getBytes());
			fos.write(("wsPort=" + wsPort + "\n").getBytes());
			fos.write(("wsToken=" + wsToken + "\n").getBytes());
			fos.write(("wsRefreshRate=" + wsRefreshRate + "\n").getBytes());
			fos.write(("wsServerID=" + wsServerId + "\n").getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
