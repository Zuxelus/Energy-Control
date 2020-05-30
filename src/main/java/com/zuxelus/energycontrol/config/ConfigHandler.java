package com.zuxelus.energycontrol.config;

import java.io.File;

import com.zuxelus.energycontrol.EnergyControl;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	public Configuration configuration;
	
	public int howlerAlarmRange;
	public int maxAlarmRange;
	public String allowedAlarms;	
	public int remoteThermalMonitorEnergyConsumption;
	public int screenRefreshPeriod;
	public int rangeTriggerRefreshPeriod;
	public int SMPMaxAlarmRange;
	public boolean disableCapes;

	public void init(File configFile) {
		if (configuration == null)
			configuration = new Configuration(configFile);
		
		loadConfiguration();
	}

	private void loadConfiguration() {
		try {
			howlerAlarmRange = configuration.get(Configuration.CATEGORY_GENERAL, "howlerAlarmRange", 64).getInt();
			maxAlarmRange = configuration.get(Configuration.CATEGORY_GENERAL, "maxAlarmRange", 128).getInt();
			allowedAlarms = configuration.get(Configuration.CATEGORY_GENERAL, "allowedAlarms", "default,sci-fi").getString().replaceAll(" ", "");
			remoteThermalMonitorEnergyConsumption = configuration.get(Configuration.CATEGORY_GENERAL, "remoteThermalMonitorEnergyConsumption", 1).getInt();
			screenRefreshPeriod = configuration.get(Configuration.CATEGORY_GENERAL, "infoPanelRefreshPeriod",20).getInt();
			rangeTriggerRefreshPeriod = configuration.get(Configuration.CATEGORY_GENERAL, "rangeTriggerRefreshPeriod", 20).getInt();
			SMPMaxAlarmRange = configuration.get(Configuration.CATEGORY_GENERAL, "SMPMaxAlarmRange", 256).getInt();
		} catch (Exception e) {
			EnergyControl.logger.error("Mod has a problem loading it's configuration", e);
		} finally {
			if (configuration.hasChanged())
				configuration.save();
		}
	}

	public void save() {
		if (configuration.hasChanged())
			configuration.save();
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(OnConfigChangedEvent event) {
		if (event.modID.equals(EnergyControl.MODID))
			loadConfiguration();
	}
}
