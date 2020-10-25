package com.zuxelus.energycontrol.config;

import java.io.File;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigHandler {
	public Configuration config;

	public int howlerAlarmRange;
	public int maxAlarmRange;
	public String allowedAlarms;	
	public int remoteThermalMonitorEnergyConsumption;
	public int infoPanelRefreshPeriod;
	public int rangeTriggerRefreshPeriod;
	public int SMPMaxAlarmRange;
	public boolean useCustomSounds;

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
			remoteThermalMonitorEnergyConsumption = config.getInt("remoteThermalMonitorEnergyConsumption", Configuration.CATEGORY_GENERAL, 1, 0, 1000, "", "ec.config.remoteThermalMonitorEnergyConsumption");
			infoPanelRefreshPeriod = config.getInt("infoPanelRefreshPeriod", Configuration.CATEGORY_GENERAL, 20, 0, 2000, "", "ec.config.screenRefreshPeriod");
			rangeTriggerRefreshPeriod = config.getInt("rangeTriggerRefreshPeriod", Configuration.CATEGORY_GENERAL, 20, 0, 2000, "", "ec.config.rangeTriggerRefreshPeriod");
			SMPMaxAlarmRange = config.getInt("SMPMaxAlarmRange", Configuration.CATEGORY_GENERAL, 256, 0, 512, "", "ec.config.SMPMaxAlarmRange");
			useCustomSounds = config.getBoolean("useCustomSounds", Configuration.CATEGORY_GENERAL, false, "", "ec.config.useCustomSounds");
		} catch (Exception e) {
			EnergyControl.logger.error("Mod has a problem loading it's configuration", e);
		} finally {
			if (config.hasChanged())
				config.save();
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(OnConfigChangedEvent event) {
		if (event.getModID().equals(EnergyControl.MODID))
			loadConfig();
	}
}
