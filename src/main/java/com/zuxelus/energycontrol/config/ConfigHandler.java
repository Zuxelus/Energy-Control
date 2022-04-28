package com.zuxelus.energycontrol.config;

import java.io.File;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigHandler {
	public Configuration config;

	public int howlerAlarmRange;
	public int maxAlarmRange;
	public String allowedAlarms;
	public int infoPanelRefreshPeriod;
	public int rangeTriggerRefreshPeriod;
	public int thermalMonitorRefreshPeriod;
	public boolean useCustomSounds;
	public int alarmPause;
	public boolean disableRangeCheck;

	public void init(File configFile) {
		if (config == null)
			config = new Configuration(configFile);
		loadConfig();
	}

	private void loadConfig() {
		try {
			howlerAlarmRange = config.getInt("howlerAlarmRange", Configuration.CATEGORY_GENERAL, 64, 0, 256, "", "ec.config.howlerAlarmRange");
			maxAlarmRange = config.getInt("maxAlarmRange", Configuration.CATEGORY_GENERAL, 128, 0, 256, "", "ec.config.maxAlarmRange");
			allowedAlarms = config.getString("allowedAlarms", Configuration.CATEGORY_GENERAL, "default,sci-fi,siren", "", "ec.config.allowedAlarms").replaceAll(" ", "");
			infoPanelRefreshPeriod = config.getInt("infoPanelRefreshPeriod", Configuration.CATEGORY_GENERAL, 20, 0, 2000, "", "ec.config.screenRefreshPeriod");
			rangeTriggerRefreshPeriod = config.getInt("rangeTriggerRefreshPeriod", Configuration.CATEGORY_GENERAL, 20, 0, 2000, "", "ec.config.rangeTriggerRefreshPeriod");
			thermalMonitorRefreshPeriod = config.getInt("thermalMonitorRefreshPeriod", Configuration.CATEGORY_GENERAL, 20, 0, 2000, "", "ec.config.thermalMonitorRefreshPeriod");
			useCustomSounds = config.getBoolean("useCustomSounds", Configuration.CATEGORY_GENERAL, false, "", "ec.config.useCustomSounds");
			alarmPause = config.getInt("alarmPause", Configuration.CATEGORY_GENERAL, 60, 0, 2000, "", "ec.config.alarmPause");
			disableRangeCheck = config.getBoolean("disableRangeCheck", Configuration.CATEGORY_GENERAL, false, "", "ec.config.disableRangeCheck");
		} catch (Exception e) {
			EnergyControl.logger.error("Mod has a problem loading it's configuration", e);
		} finally {
			if (config.hasChanged())
				config.save();
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(EnergyControl.MODID))
			loadConfig();
	}
}
